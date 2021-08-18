package com.downing.util.gui;

import com.downing.util.config.TheadPool;
import com.downing.util.entity.DownInfo;
import com.downing.util.service.DownService3;
import com.downing.util.util.FileUtil;
import com.downing.util.util.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class GuiMain {

    /**
     * UI 的绘制
     */
    private JFrame jFrame;
    private JButton searchBn;
    private JButton fileChooseBn;
    private JButton downloadBn;

    private JTextField urlField;
    private JTextField filePathField;
    private JTextField statusField;

    //进度条
    private JProgressBar downloadProgress;

    private Font font;

    /**
     * 该下载器的尺寸
     */
    private int width = 600;
    private int height = 400;
    /**
     * 屏幕尺寸
     */
    private int screenWidth;
    private int screenHeight;

    /**
     * 下载路径
     */
    private String urlPath;

    /**
     * 文件保存路径
     */
    private String fileToSave;

    private final String TAG = "状态: ";

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 文件类型
     */
    private String contentType;

    private void init() {
        //使整个UI和平台相关，可以美化Windows环境下的JFileChooser
        if (UIManager.getLookAndFeel().isSupportedLookAndFeel()) {
            final String platform = UIManager.getSystemLookAndFeelClassName();
            if (!UIManager.getLookAndFeel().getName().equals(platform)) {
                try {
                    UIManager.setLookAndFeel(platform);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        jFrame = new JFrame("多线程下载器v3.0");
        //设置界面在屏幕正中间
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();
        jFrame.setBounds((screenWidth - width) / 2,
                (screenHeight - height) / 2,
                width, height);
        jFrame.setResizable(false);

        //该字体将被所有控件使用
        font = new Font("微软雅黑", 0, 13);

        //设置为4*1的网格布局管理器
        jFrame.setLayout(new GridLayout(4, 1));

        //第一个JPanel进行链接的验证
        JPanel downloadPathPanel = new JPanel();
        downloadPathPanel.setBackground(Color.WHITE);
        JLabel label1 = new JLabel("下载链接:");
        label1.setFont(font);
        urlField = new JTextField(20);
        urlField.setFont(font);
        searchBn = new JButton("验证");
        searchBn.setBackground(Color.WHITE);
        searchBn.setFont(font);
        searchBn.setFocusPainted(false);
        searchBn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(urlField.getText().trim().equals("") || urlField.getText() == null)) {
                    //FutureTask的get方法是个阻塞方法，新开一个线程对其处理比较好
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                urlPath = urlField.getText().trim();
                                statusField.setText(TAG + "已验证链接");
                                Thread.sleep(1000);
                                if (fileSize <= 0) {
                                    statusField.setText(TAG + "链接无效");
                                } else {
                                    statusField.setText(TAG + "文件类型:" + contentType + ", 文件大小:" + FileUtil.getFormatFileSize(fileSize));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    statusField.setText(TAG + "请输入下载链接");
                }
            }
        });
        downloadPathPanel.add(label1);
        downloadPathPanel.add(urlField);
        downloadPathPanel.add(searchBn);
        jFrame.add(downloadPathPanel);

        /**
         * 第二个面板
         * 进行保存路径的设置
         */
        JPanel savePathPanel = new JPanel();
        savePathPanel.setBackground(Color.WHITE);
        JLabel label2 = new JLabel("保存路径:");
        label2.setFont(font);
        filePathField = new JTextField(20);
        filePathField.setFont(font);
        fileChooseBn = new JButton("路径");
        fileChooseBn.setBackground(Color.WHITE);
        fileChooseBn.setFont(font);
        fileChooseBn.setFocusPainted(false);
        fileChooseBn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFont(font);
                chooser.setDialogTitle("选择保存文件夹");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(jFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    if (getFileName() == null) {
                        fileToSave = chooser.getSelectedFile().getPath();
                    } else {
                        fileToSave = Paths.get(chooser.getSelectedFile().getPath(), getFileName()).toString();
                        statusField.setText(TAG + "已选择" + fileToSave);
                    }
                    System.out.println(fileToSave);
                    filePathField.setText(fileToSave);

                }
            }
        });
        savePathPanel.add(label2);
        savePathPanel.add(filePathField);
        savePathPanel.add(fileChooseBn);
        jFrame.add(savePathPanel);

        //第三个面板进度条
        downloadProgress = new JProgressBar();
        downloadProgress.setMinimum(0);
        downloadProgress.setMaximum(100);
        downloadProgress.setString("未开始下载...");
        downloadProgress.setFont(font);
        downloadProgress.setValue(0);
        downloadProgress.setStringPainted(true);
        downloadProgress.setPreferredSize(new Dimension(305, 25));
        downloadProgress.setBorderPainted(true);
        downloadProgress.setBackground(Color.WHITE);
        downloadProgress.setForeground(new Color(0x6959CD));
        JPanel downPanel = new JPanel();
        downPanel.setBackground(Color.WHITE);
        downloadBn = new JButton("下载");
        downloadBn.setBackground(Color.WHITE);
        downloadBn.setFocusPainted(false);
        downloadBn.setFont(font);
        downloadBn.addActionListener(e -> {
            urlPath = urlPath.equals(urlField.getText().trim()) ? urlPath : urlField.getText().trim();
            fileToSave = fileToSave.equals(filePathField.getText().trim()) ? fileToSave : filePathField.getText().trim();
            if (urlPath == null) {
                statusField.setText(TAG + "下载失败，请填写链接和路径");
                return;
            }
            DownService3 downService2 = new DownService3(fileToSave + "\\", 10);
            //新开一个线程监控下载情况 并在进度条上反映出来
            /*new Thread(() -> {
                double schedule;
                statusField.setText(TAG + "正在下载...");
                long start = System.currentTimeMillis();
                while ((schedule = downService2.getCurrentProgress()) < 1d) {
                    downloadProgress.setValue((int) (schedule * 100));
                    downloadProgress.setString(String.format("%.2f", schedule * 100) + "%");
                    System.out.println("当前进度：" + String.format("%.2f", schedule * 100) + "%" + "---数字：" + (int) (schedule * 100));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                long end = System.currentTimeMillis();
                downloadProgress.setValue(100);
                downloadProgress.setString("下载完成!");
                statusField.setText(TAG + "下载用时: " + TimeUtil.executeTime(end - start));
            }).start();*/

            /*worker = new SwingWorker<Object, String>() {
                @Override
                protected Object doInBackground() throws Exception {
                    statusField.setText(TAG + "正在下载...");
                    long start = System.currentTimeMillis();
                    downloadProgress.addChangeListener(new ChangeListener() {
                        double schedule;
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            if (downService2.getCurrentProgress() < 1d) {
                                while ((schedule = downService2.getCurrentProgress()) < 1d) {
                                    downloadProgress.setValue((int) (schedule * 100));
                                    downloadProgress.setString(String.format("%.2f", schedule * 100) + "%");
                                    System.out.println("当前进度：" + String.format("%.2f", schedule * 100) + "%" + "---数字：" + (int) (schedule * 100));
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

                    long end = System.currentTimeMillis();
                    downloadProgress.setValue(100);
                    downloadProgress.setString("下载完成!");
                    statusField.setText(TAG + "下载用时: " + TimeUtil.executeTime(end - start));
                    return null;
                }
            };
            worker.execute();*/

            Thread worker = new Thread(() -> {
                statusField.setText(TAG + "正在下载...");
                long start = System.currentTimeMillis();
                double schedule;
                while ((schedule = downService2.getCurrentProgress()) < 1d) {
                    downloadProgress.setValue((int) (schedule * 100));
                    downloadProgress.setString(String.format("%.2f", schedule * 100) + "%");
                    System.out.println("当前进度：" + String.format("%.2f", schedule * 100) + "%" + "---数字：" + (int) (schedule * 100));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                long end = System.currentTimeMillis();
                downloadProgress.setValue(100);
                downloadProgress.setString("下载完成!");
                statusField.setText(TAG + "下载用时: " + TimeUtil.executeTime(end - start));
            });
            TheadPool.getExecutor().execute(worker);

            statusField.setText(TAG + "正在准备下载...");
            DownInfo info = downService2.start(urlPath);
            statusField.setText(TAG + info.getMessage());
        });
        downPanel.add(downloadProgress);
        downPanel.add(downloadBn);
        jFrame.add(downPanel);

        /**
         * 第四个面板状态信息
         */
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.WHITE);
        statusField = new JTextField(31);
        statusField.setText("状态: ");
        statusField.setBorder(null);
        statusField.setEditable(false);
        statusField.setFont(font);
        statusField.setBackground(Color.WHITE);
        statusPanel.add(statusField);
        jFrame.add(statusPanel);

        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);

        /**
         * 在启动时查看系统剪贴板中的内容
         * 如果有链接，就直接复制到urlField上去
         */
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipTf = sysClip.getContents(null);
        if (clipTf != null) {
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
                    String check = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
                    if (Pattern.matches(check, ret)) {
                        statusField.setText(TAG + "检测到剪贴板的链接");
                        urlField.setText(ret);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public GuiMain() {
        init();
    }

    public static void main(String[] args) {
        new GuiMain();
    }

    /**
     * 通过URL解析或者content-type转换获取文件名，用户也可以自定义文件名
     */
    private String getFileName() {
        if ((urlPath = urlField.getText()).equals("") || urlPath == null) {
            statusField.setText(TAG + "请输入下载链接");
            return null;
        } else {
            int pos = urlPath.lastIndexOf("/") + 1;
            String name = urlPath.substring(pos);
            if (Pattern.matches("^\\S{3,20}\\.\\S{3,10}", name)) {
                return name;
            }
        }
        return null;
    }
}