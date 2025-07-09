package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import io.*;
import model.AppState;
import model.Message;
import model.User;

public class LoginUI extends JFrame {
    public LoginUI() {
        setTitle("登录");
        setSize(420, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false); // 保留关闭按钮
        setMaximumSize(new Dimension(420, 360));
        try{
            ImageIcon icon = new ImageIcon(getClass().getResource("/default_2.jpg"));
            setIconImage(icon.getImage());
        }catch(Exception e){
            System.out.println("加载ico失败");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("欢迎使用聊天系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
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
        gbc.insets = new Insets(10, 4, 10, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField ipField = new JTextField("127.0.0.1");
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

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("服务器 IP:", JLabel.LEFT) {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(ipField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("端口号:", JLabel.LEFT) {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(portField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("用户名:", JLabel.LEFT) {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("密码:", JLabel.LEFT) {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

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
        NetIO netIO=new NetIO();
        loginButton.addActionListener(e -> {
            loginButton.disable();
            String ip = ipField.getText().trim();
            String port = portField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (ip.isEmpty() || port.isEmpty()) {
                JOptionPane.showMessageDialog(this, "IP 和端口号不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(netIO.connect(ip,Integer.parseInt(port))){
                if (netIO.connectAndLogin(ip, Integer.parseInt(port), username, password)) {
                    AppState.getInstance().setNetIO(netIO);
                    AppState.getInstance().setCurrentUser(new User(username));
                    ChatUI.main(null); // 登录成功
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                    netIO.disconnect();
                    loginButton.enable();
                }
            }else{
                JOptionPane.showMessageDialog(this, "无法连接到你填写的服务器", "登陆失败", JOptionPane.ERROR_MESSAGE);
                loginButton.enable();
            }


        });
        // 添加回车触发登录
        // 定义一个ActionListener，触发登录按钮点击
        ActionListener enterListener = e -> loginButton.doClick();

        ipField.addActionListener(enterListener);
        portField.addActionListener(enterListener);
        usernameField.addActionListener(enterListener);
        passwordField.addActionListener(enterListener);




        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "注册功能暂未实现"));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        }
}