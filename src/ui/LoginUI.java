package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import chat.Chat;
import com.google.gson.JsonParser;
import io.*;
import model.AppState;
import model.Message;
import model.User;

public class LoginUI extends JFrame {
    private static final String DEFAULT_SERVER_IP = "chatclient.asia";
    private static final int DEFAULT_SERVER_PORT = 8080;

    public LoginUI() {
        setTitle("登录");
        setSize(420, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false); // 保留关闭按钮
        setMaximumSize(new Dimension(420, 360));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/default_2.jpg"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("加载ico失败");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("ChatClient-1.0", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.white);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.fill = GridBagConstraints.NONE;
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 0;
        gbcCenter.gridwidth = 2;
        gbcCenter.anchor = GridBagConstraints.CENTER;
        gbcCenter.insets = new Insets(10, 4, 20, 4);
        formPanel.add(new JLabel("", JLabel.CENTER), gbcCenter);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 统一边距
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField ipField = new JTextField("");
        ipField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ipField.setPreferredSize(new Dimension(280, 36));
        JTextField portField = new JTextField("8080");
        portField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        portField.setPreferredSize(new Dimension(280, 36));
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(280, 36));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(280, 36));

        Font labelFont = new Font("微软雅黑", Font.PLAIN, 14);


        // ===== 统一输入框尺寸 =====
        Dimension inputSize = new Dimension(280, 36);
        ipField.setPreferredSize(inputSize);
        portField.setPreferredSize(inputSize);
        usernameField.setPreferredSize(inputSize);
        passwordField.setPreferredSize(inputSize);
        // 高级设置面板（服务器设置）
        JPanel advancedPanel = new JPanel(new GridBagLayout());
        advancedPanel.setBackground(Color.white);
        advancedPanel.setVisible(false); // 初始隐藏
        advancedPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        //高级设置：服务器 IP
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        advancedPanel.add(new JLabel("服务器 IP:") {{ setFont(labelFont); }}, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        advancedPanel.add(ipField, gbc);

        // 高级设置：端口号
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        advancedPanel.add(new JLabel("端口号:") {{ setFont(labelFont); }}, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        advancedPanel.add(portField, gbc);




        // 用户名
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        formPanel.add(new JLabel("用户名:") {{ setFont(labelFont); }}, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        //密码
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("密码:") {{ setFont(labelFont); }}, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(advancedPanel, gbc);
        //记住密码复选框
        JCheckBox rememberCheckBox = new JCheckBox("记住密码");
        rememberCheckBox.setFont(labelFont);
        rememberCheckBox.setBackground(Color.white);
        JCheckBox showAdvancedCheckBox = new JCheckBox("显示高级设置");
        showAdvancedCheckBox.setFont(labelFont);
        showAdvancedCheckBox.setBackground(Color.white);
        showAdvancedCheckBox.addActionListener(e -> {
            ipField.setText(null);
            portField.setText(null);
            advancedPanel.setVisible(showAdvancedCheckBox.isSelected());
            pack(); // 调整窗口大小

        });

        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(rememberCheckBox, gbc);
        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(showAdvancedCheckBox, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
//        JButton advancedButton = new JButton("高级") {
//            @Override
//            protected void paintComponent(Graphics g) {
//                g.setColor(getModel().isRollover() ? new Color(230, 230, 230) : Color.WHITE);
//                g.fillRect(0, 0, getWidth(), getHeight());
//                g.setColor(Color.BLACK);
//                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//                super.paintComponent(g);
//            }
//        };
//        advancedButton.setBackground(Color.white);
//        advancedButton.setFocusPainted(false);
//        advancedButton.setBorder(BorderFactory.createLineBorder(Color.black));
//        advancedButton.setContentAreaFilled(false);
//        advancedButton.setOpaque(true);
//        advancedButton.setPreferredSize(new Dimension(100, 36));
//
//        advancedButton.addActionListener(e -> {
//            ipField.setText(null);
//            portField.setText(null);
//            advancedPanel.setVisible(!advancedPanel.isVisible());
//            pack(); // 调整窗口大小
//        });

        JButton loginButton = new JButton("登录") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isRollover() ? new Color(230, 230, 230) : Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
            }
        };
        loginButton.setBackground(Color.white);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);

        JButton registerButton = new JButton("注册") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isRollover() ? new Color(230, 230, 230) : Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
            }
        };
        registerButton.setBackground(Color.white);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createLineBorder(Color.black));
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(true);


        loginButton.setPreferredSize(new Dimension(100, 36));
        registerButton.setPreferredSize(new Dimension(100, 36));
        loginButton.addActionListener(e -> {
            // 禁用按钮防止重复点击
            loginButton.setEnabled(false);

//            String ip = ipField.getText().trim();
//            String port = portField.getText().trim();
            // 改为使用默认值
            String ip = advancedPanel.isVisible() ? ipField.getText().trim() : DEFAULT_SERVER_IP;
            String port = advancedPanel.isVisible() ? portField.getText().trim() : String.valueOf(DEFAULT_SERVER_PORT);


            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // 空值检查
            if (ip.isEmpty() || port.isEmpty()) {
                JOptionPane.showMessageDialog(this, "IP 和端口号不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                loginButton.setEnabled(true); // 恢复按钮
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                loginButton.setEnabled(true); // 恢复按钮
                return;
            }

            // 开线程处理连接和登录逻辑，避免阻塞UI
            new Thread(() -> {
                try {
                    if (NetIO.getInstance().connect(ip, Integer.parseInt(port))) {
                        if (NetIO.getInstance().connectAndLogin(ip, Integer.parseInt(port), username, password)) {
                            AppState.getInstance().setNetIO(NetIO.getInstance());
                            AppState.getInstance().setCurrentUser(new User(username,password));
                            SwingUtilities.invokeLater(() -> {
                                if (rememberCheckBox.isSelected()) {
                                    RememberMe.save(username, password);
                                } else {
                                    RememberMe.clear();
                                }

                                ChatUI.main(null); // 登录成功进入主界面
                                dispose(); // 关闭登录窗口
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                                NetIO.getInstance().disconnect();
                                loginButton.setEnabled(true); // 恢复按钮
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "无法连接到你填写的服务器", "登录失败", JOptionPane.ERROR_MESSAGE);
                            loginButton.setEnabled(true); // 恢复按钮
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "登录异常：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        loginButton.setEnabled(true); // 恢复按钮
                    });
                }
            }).start();
        });

        // 添加回车触发登录
        // 定义一个ActionListener，触发登录按钮点击
        ActionListener enterListener = e -> loginButton.doClick();

        ipField.addActionListener(enterListener);
        portField.addActionListener(enterListener);
        usernameField.addActionListener(enterListener);
        passwordField.addActionListener(enterListener);


        registerButton.addActionListener(e -> {
            String ip = advancedPanel.isVisible() ? ipField.getText().trim() : DEFAULT_SERVER_IP;
            String port = advancedPanel.isVisible() ? portField.getText().trim() : String.valueOf(DEFAULT_SERVER_PORT);
            if (ip.isEmpty() || port.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先填写 IP 和端口", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String regUsername = JOptionPane.showInputDialog(this, "请输入注册用户名：");
            if (regUsername == null || regUsername.trim().isEmpty()) return;

            String regPassword = JOptionPane.showInputDialog(this, "请输入注册密码：");
            if (regPassword == null || regPassword.trim().isEmpty()) return;

            // 禁用注册按钮，避免重复点击
            registerButton.setEnabled(false);

            // 开新线程执行注册逻辑
            new Thread(() -> {
                try {
                    if (!NetIO.getInstance().isConnected()) {
                        if (!NetIO.getInstance().connect(ip, Integer.parseInt(port))) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "连接服务器失败", "注册失败", JOptionPane.ERROR_MESSAGE);
                                registerButton.setEnabled(true); // 恢复按钮
                            });
                            return;
                        }
                    }

                    Chat.sendRegister(regUsername, regPassword);

                    String reply = NetIO.getInstance().receive();
                    Message response = new Message(JsonParser.parseString(reply).getAsJsonObject());

                    SwingUtilities.invokeLater(() -> {
                        if ("ok".equals(response.getStatus()) && "create".equals(response.getMessage())) {
                            JOptionPane.showMessageDialog(this, "注册成功！现在可以登录了", "成功", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "注册失败：" + response.getMessage(), "注册失败", JOptionPane.ERROR_MESSAGE);
                        }
                        registerButton.setEnabled(true); // 恢复按钮
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "注册失败：" + ex.getMessage(), "注册异常", JOptionPane.ERROR_MESSAGE);
                        registerButton.setEnabled(true); // 恢复按钮
                    });
                } finally {
                    NetIO.getInstance().disconnect();
                }
            }).start();
        });



        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
//        buttonPanel.add(advancedButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        String[] saved = RememberMe.load();
        if (saved != null) {
            usernameField.setText(saved[0]);
            passwordField.setText(saved[1]);
            rememberCheckBox.setSelected(true);
        }

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}