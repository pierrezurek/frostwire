/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2018, FrostWire(R). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.limegroup.gnutella.gui;

import com.frostwire.bittorrent.BTEngine;
import com.frostwire.jlibtorrent.EnumNet;
import org.apache.commons.io.IOUtils;
import org.limewire.util.OSUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author gubatron
 * @author aldenml
 */
public final class VPNs {

    private static String netstatCmd = null;

    public static boolean isVPNActive() {
        boolean result = false;

        if (OSUtils.isMacOSX() || OSUtils.isLinux()) {
            result = isPosixVPNActive();
        } else if (OSUtils.isWindows()) {
            result = isWindowsVPNActive();
        }

        return result;
    }

    /**
     * <strong>VPN ON (Mac)</strong>
     * <pre>Internet:
     * Destination        Gateway            Flags        Refs      Use   Netif Expire
     * 0/1                10.81.10.5         UGSc            5        0   utun1
     * ...</pre>
     *
     * <strong>VPN ON (Linux)</strong>
     * <pre>Kernel IP routing table
     * Destination     Gateway         Genmask         Flags   MSS Window  irtt Iface
     * 0.0.0.0         10.31.10.5      128.0.0.0       UG        0 0          0 tun0
     * ...</pre>
     *
     * @return true if it finds a line that starts with "0" and contains "tun" in the output of "netstat -nr"
     */
    private static boolean isPosixVPNActive() {
        boolean result = false;
        try {
            List<EnumNet.IpRoute> routes = EnumNet.enumRoutes(BTEngine.getInstance());
            for (EnumNet.IpRoute route : routes) {
                if (route.destination().toString().equals("0.0.0.0") && route.name().contains("tun")) {
                    result = true;
                    break;
                }
            }
        } catch (Throwable t) {
            result = false;
        }

        return result;
    }

    private static boolean isWindowsVPNActive() {
        try {
            List<EnumNet.IpInterface> interfaces = EnumNet.enumInterfaces(BTEngine.getInstance());
            List<EnumNet.IpRoute> routes = EnumNet.enumRoutes(BTEngine.getInstance());
            return isWindowsPIAActive(interfaces, routes) || isExpressVPNActive(interfaces);
        } catch (Throwable t2) {
            t2.printStackTrace();
            return false;
        }
    }

    private static boolean isWindowsPIAActive(List<EnumNet.IpInterface> interfaces, List<EnumNet.IpRoute> routes) {
        // Try looking for an active PIA Interface "TAP-Windows Adapter*"
        EnumNet.IpInterface tapWindowsAdapter = null;
        for (EnumNet.IpInterface iface : interfaces) {
            if (iface.description().contains("TAP-Windows Adapter") && iface.preferred()) {
                tapWindowsAdapter = iface;
                break;
            }
        }

        if (tapWindowsAdapter == null) {
            return false;
        }

        // Look for the tapWindowsAdapter in the list of active routes
        for (EnumNet.IpRoute route : routes) {
            if (route.name().contains(tapWindowsAdapter.name())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isExpressVPNActive(List<EnumNet.IpInterface> interfaces) {
        boolean expressVPNTapAdapterPresent = false;
        for (EnumNet.IpInterface iface : interfaces) {
            if (iface.description().contains("ExpressVPN Tap Adapter") && iface.preferred()) {
                expressVPNTapAdapterPresent = true;
                break;
            }
        }
        return expressVPNTapAdapterPresent;
    }

    private static String readProcessOutput(String command, String arguments) {
        String result = "";
        ProcessBuilder pb = new ProcessBuilder(command, arguments);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            InputStream stdout = process.getInputStream();
            final BufferedReader brstdout = new BufferedReader(new InputStreamReader(stdout));
            String line;

            try {
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = brstdout.readLine()) != null) {
                    stringBuilder.append(line).append("\r\n");
                }

                result = stringBuilder.toString();
            } catch (Exception e) {
                // ignore
            } finally {
                IOUtils.closeQuietly(brstdout);
                IOUtils.closeQuietly(stdout);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getNetstatPath() {
        if (netstatCmd != null) {
            return netstatCmd;
        }
        String candidate = "netstat";
        if (OSUtils.isMacOSX() && new File("/usr/sbin/netstat").exists()) {
            candidate = "/usr/sbin/netstat";
        }
        netstatCmd = candidate;
        return netstatCmd;
    }
}
