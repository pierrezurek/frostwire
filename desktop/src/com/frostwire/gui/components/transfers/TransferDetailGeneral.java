/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml),
 * Marcelina Knitter (@marcelinkaaa), Jose Molina (@votaguz)
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

package com.frostwire.gui.components.transfers;

import com.frostwire.bittorrent.BTDownload;
import com.frostwire.gui.bittorrent.BTDownloadDataLine;
import com.frostwire.gui.bittorrent.BittorrentDownload;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.frostwire.jlibtorrent.TorrentStatus;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.I18n;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public final class TransferDetailGeneral extends JPanel implements TransferDetailComponent.TransferDetailPanel {
    private final JProgressBar completionPercentageProgressbar;
    private final JLabel timeElapsedLabel;
    private final JLabel torrentNameLabel;
    private final JLabel completionPercentageLabel;
    private final JLabel timeLeftLabel;
    private final JLabel downloadSpeedLabel;
    private final JLabel downloadedLabel;
    private final JLabel statusLabel;
    private final JLabel downloadSpeedLimitLabel;
    private final JLabel uploadedLabel;
    private final JLabel seedsLabel;
    private final JLabel uploadSpeedLabel;
    private final JLabel totalSizeLabel;
    private final JLabel peersLabel;
    private final JLabel uploadSpeedLimitLabel;
    private final JLabel shareRatioLabel;
    private final JLabel saveLocationLabel;
    private final JLabel infoHashLabel;
    private final JButton copyInfoHashButton;
    private final JLabel magnetURLLabel;
    private final JButton copyMagnetURLButton;
    private final JLabel createdOnLabel;
    private final JLabel commentLabel;

    private ActionListener copyInfoHashActionListener;
    private ActionListener copyMagnetURLActionListener;

    TransferDetailGeneral() {
        //MigLayout Notes:
        // insets -> padding for the layout
        // gap -> space/margin _between cells_ in the layout, if you have
        //        a different background in inner components than the
        //        container, the opaque background of the container will leak in between
        //        cells
        //
        // API inconsistencies:
        // (Layout) insets <top/all left bottom right>
        // (Layout) gap <x y>
        // (Component) gap <left right top bottom> (FML)
        // (Component) pad <top left bottom right> (like insets, why not just re-use insets, FML)


        super(new MigLayout("insets 0 0 0 0, gap 0 0, fill"));
        // Upper panel with Name, Percentage labels [future share button]
        // progress bar
        // slightly darker background color (0xf3f5f7)

        JPanel upperPanel = new JPanel(new MigLayout("insets 5px, gap 0 5px, fill"));
        upperPanel.setBackground(new Color(0xf3f5f7));
        upperPanel.setOpaque(true);
        upperPanel.add(new JLabel("<html><b>" + I18n.tr("Name") + "</b></html>"), "left, gapleft 15px, gapright 15px");
        upperPanel.add(torrentNameLabel = new JLabel(""), "left, gapright 15px");
        upperPanel.add(new JLabel("|"), "left, gapright 15px");
        upperPanel.add(completionPercentageLabel = new JLabel("<html><b>0%</b></html>"),"left, gapright 5px");
        upperPanel.add(new JLabel("<html><b>" + I18n.tr("complete") + "</b></html>"), "left, pushx, wrap");
        upperPanel.add(completionPercentageProgressbar = new JProgressBar(),"span 5, growx");

        // 2nd Section (TRANSFER)
        JPanel midPanel = new JPanel(new MigLayout("insets 5px 0 0 0, gap 5px 5px, fill"));
        midPanel.setBackground(Color.WHITE);
        midPanel.setOpaque(true);
        // time elapsed, time left, download speed
        midPanel.add(new JGrayLabel(I18n.tr("Time elapsed")), "left, pad 0 10px 0 0, growx");
        midPanel.add(timeElapsedLabel = new JLabel(),"left");
        midPanel.add(new JGrayLabel(I18n.tr("Time left")), "left");
        midPanel.add(timeLeftLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Download speed")),"left");
        midPanel.add(downloadSpeedLabel = new JLabel(),"left, wrap");

        // Downloaded, status, download speed limit
        midPanel.add(new JGrayLabel(I18n.tr("Downloaded")), "left, pad 0 10px 0 0, growx");
        midPanel.add(downloadedLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Status")), "left");
        midPanel.add(statusLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Download speed limit")),"left");
        midPanel.add(downloadSpeedLimitLabel = new JLabel(), "left, wrap");
        // TODO: Add settings_gray button and dialog to adjust download speed limit

        // Uploaded, seeds, upload speed
        midPanel.add(new JGrayLabel(I18n.tr("Uploaded")), "left, pad 0 10px 0 0, growx");
        midPanel.add(uploadedLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Seeds")), "left");
        midPanel.add(seedsLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Upload speed")), "left");
        midPanel.add(uploadSpeedLabel = new JLabel(), "gap 10px 10px 0 0, left, wrap");

        // Total Size, Peers, Upload speed limit
        // Share Ratio
        midPanel.add(new JGrayLabel(I18n.tr("Total size")), "left, pad 0 10px 0 0, growx");
        midPanel.add(totalSizeLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Peers")), "left");
        midPanel.add(peersLabel = new JLabel(), "left");
        midPanel.add(new JGrayLabel(I18n.tr("Upload speed limit")), "left");
        midPanel.add(uploadSpeedLimitLabel = new JLabel(), "left, wrap");
        // TODO: Add settings_gray button and dialog to adjust upload speed limit
        midPanel.add(new JGrayLabel(I18n.tr("Share ratio")), "left, pad 0 10px 0 0, growx");
        midPanel.add(shareRatioLabel = new JLabel(), "wrap");

        // 3rd Section, "GENERAL"
        // Save location
        // InfoHash
        // Magnet URL
        // Created On
        // Comment
        JPanel lowerPanel = new JPanel(new MigLayout("insets 0 0 0 0, gap 5px 5px, fill"));
        lowerPanel.setBackground(Color.WHITE);
        lowerPanel.setOpaque(true);

        lowerPanel.add(new JGrayLabel(I18n.tr("Save location")), "left, wmin 160px, pad 0 10px 0 0, growx");
        lowerPanel.add(saveLocationLabel = new JLabel(), "left, growx, span 2, wrap");

        final ImageIcon copy_paste_gray = GUIMediator.getThemeImage("copy_paste_gray.png");
        final ImageIcon copy_paste = GUIMediator.getThemeImage("copy_paste.png");

        lowerPanel.add(new JGrayLabel(I18n.tr("InfoHash")), "left, gapright 10px, pad 0 10px 0 0, growx");
        lowerPanel.add(infoHashLabel = new JLabel(), "left");
        lowerPanel.add(copyInfoHashButton = new JButton(copy_paste_gray),"left, pushx, wrap");

        lowerPanel.add(new JGrayLabel(I18n.tr("Magnet URL")), "left, gapright 10px, pad 0 10px 0 0, growx");
        lowerPanel.add(magnetURLLabel = new JLabel(), "left");
        lowerPanel.add(copyMagnetURLButton = new JButton(copy_paste_gray),"left, pushx, wrap");

        lowerPanel.add(new JGrayLabel(I18n.tr("Created On")), "left, gapright 10px, pad 0 10px 0 0, growx");
        lowerPanel.add(createdOnLabel = new JLabel(), "left, growx, span 2, wrap");

        lowerPanel.add(new JGrayLabel(I18n.tr("Comment")), "left, gapright 10px, pad 0 10px 0 0, growx");
        lowerPanel.add(commentLabel = new JLabel(), "left, growx, span 2, wrap");

        copyInfoHashButton.setPressedIcon(copy_paste);
        copyInfoHashButton.setContentAreaFilled(false);
        copyMagnetURLButton.setContentAreaFilled(false);
        copyMagnetURLButton.setPressedIcon(copy_paste);

        setOpaque(true);
        // Empty border for margins, and line border for visual delimiter
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10,10,10,10),
                BorderFactory.createLineBorder(new Color(0x9297a1))));
        add(upperPanel, "top, growx, growprioy 0, gapbottom 5px, wrap");
        add(midPanel, "gap 0 0 0 0, growx, growprioy 0, wrap");
        add(lowerPanel, "gap 0 0 0 0, grow");
    }

    @Override
    public void updateData(BittorrentDownload guiBtDownload) {
        if (guiBtDownload == null) {
            return;
        }

        BTDownload btDownload = guiBtDownload.getDl();
        TorrentHandle torrentHandle = btDownload.getTorrentHandle();
        TorrentStatus status = torrentHandle.status();
        TorrentInfo torrentInfo = torrentHandle.torrentFile();

        torrentNameLabel.setText(btDownload.getName());
        int progress = btDownload.getProgress();
        completionPercentageLabel.setText("<html><b>" + progress + "%</b></html>");
        completionPercentageProgressbar.setMaximum(100);
        completionPercentageProgressbar.setValue(progress);

        timeElapsedLabel.setText(seconds2time(status.activeDuration()/1000));
        timeLeftLabel.setText(seconds2time(guiBtDownload.getETA()));
        downloadSpeedLabel.setText(GUIUtils.getBytesInHuman(btDownload.getDownloadSpeed()));

        downloadedLabel.setText(GUIUtils.getBytesInHuman(btDownload.getTotalBytesReceived()));
        statusLabel.setText(BTDownloadDataLine.TRANSFER_STATE_STRING_MAP.get(btDownload.getState()));
        downloadSpeedLimitLabel.setText(GUIUtils.getBytesInHuman(btDownload.getDownloadRateLimit()));

        uploadedLabel.setText(GUIUtils.getBytesInHuman(btDownload.getTotalBytesSent()));
        seedsLabel.setText(String.format("%d %s %s %d %s",
                btDownload.getConnectedSeeds(),
                I18n.tr("connected"),
                I18n.tr("of"),
                btDownload.getTotalSeeds(),
                I18n.tr("total")));
        uploadSpeedLabel.setText(GUIUtils.getBytesInHuman(btDownload.getUploadSpeed()));

        totalSizeLabel.setText(GUIUtils.getBytesInHuman(btDownload.getSize()));
        peersLabel.setText(String.format("%d %s %s %d %s",
                btDownload.getConnectedPeers(),
                I18n.tr("connected"),
                I18n.tr("of"),
                btDownload.getTotalPeers(),
                I18n.tr("total")));
        uploadSpeedLimitLabel.setText(GUIUtils.getBytesInHuman(btDownload.getUploadRateLimit()));

        shareRatioLabel.setText(guiBtDownload.getShareRatio());

        saveLocationLabel.setText(guiBtDownload.getSaveLocation().getAbsolutePath());

        infoHashLabel.setText(btDownload.getInfoHash());
        if (copyInfoHashActionListener != null) {
            copyInfoHashButton.removeActionListener(copyInfoHashActionListener);
        }
        copyInfoHashActionListener = e -> GUIMediator.setClipboardContent(btDownload.getInfoHash());
        copyInfoHashButton.addActionListener(copyInfoHashActionListener);

        String magnetURI = btDownload.magnetUri();
        if (magnetURI.length() > 50) {
            magnetURLLabel.setText(magnetURI.substring(0,49) + "...");
        } else {
            magnetURLLabel.setText(magnetURI);
        }
        if (copyMagnetURLActionListener != null) {
            copyMagnetURLButton.removeActionListener(copyMagnetURLActionListener);
        }
        copyMagnetURLActionListener = e -> GUIMediator.setClipboardContent(magnetURI);
        copyMagnetURLButton.addActionListener(copyMagnetURLActionListener);

        createdOnLabel.setText(btDownload.getCreated().toString());
        commentLabel.setText(torrentInfo.comment());
    }

    /**
     * Converts a value in seconds to:
     * "d:hh:mm:ss" where d=days, hh=hours, mm=minutes, ss=seconds, or
     * "h:mm:ss" where h=hours<24, mm=minutes, ss=seconds, or
     * "m:ss" where m=minutes<60, ss=seconds
     */
    private String seconds2time(long seconds) {
        if (seconds == -1) {
            return "∞";
        }
        long minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        long hours = minutes / 60;
        minutes = minutes - hours * 60;
        long days = hours / 24;
        hours = hours - days * 24;
        // build the numbers into a string
        StringBuilder time = new StringBuilder();
        if (days != 0) {
            time.append(Long.toString(days));
            time.append(":");
            if (hours < 10)
                time.append("0");
        }
        if (days != 0 || hours != 0) {
            time.append(Long.toString(hours));
            time.append(":");
            if (minutes < 10)
                time.append("0");
        }
        time.append(Long.toString(minutes));
        time.append(":");
        if (seconds < 10)
            time.append("0");
        time.append(Long.toString(seconds));
        return time.toString();
    }

    private static class JGrayLabel extends JLabel {
        JGrayLabel(String html) {
            super(html);
            setForeground(Color.GRAY);
        }
    }
}