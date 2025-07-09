package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class InformationUI extends JFrame {
    private JTextField usernameField, nicknameField, friendCountField;
    private JButton editButton, saveButton, okButton;
    private boolean isEditing = false;
    private String nickName = "竹子";

    public InformationUI() {
        setTitle("个人信息");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        try{
            ImageIcon icon = new ImageIcon(getClass().getResource("/default_2.jpg"));
            setIconImage(icon.getImage());
        }catch(Exception e){
            System.out.println("加载ico失败");
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("个人信息", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 4, 10, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("微软雅黑", Font.PLAIN, 14);

        usernameField = new JTextField("2025001");
        nicknameField = new JTextField(nickName);
        friendCountField = new JTextField("12");

        JTextField[] fields = {usernameField, nicknameField, friendCountField};
        for (JTextField field : fields) {
            field.setFont(labelFont);
            field.setPreferredSize(new Dimension(320, 36));
            field.setEditable(false);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("用户名:") {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("昵 称:") {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nicknameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("好友数量:") {{ setFont(labelFont); }}, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(friendCountField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        editButton = createFlatButton("编辑");
        saveButton = createFlatButton("保存");
        saveButton.setVisible(false);
        okButton = createFlatButton("确定");

        editButton.addActionListener(e -> {
            if (!isEditing) {
                nicknameField.setEditable(true);
                nicknameField.requestFocus();
                isEditing = true;
                okButton.setText("取消");
            }
        });

        nicknameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                boolean changed = !nicknameField.getText().equals(nickName);
                saveButton.setVisible(isEditing && changed);
            }
        });

        saveButton.addActionListener((ActionEvent e) -> {
            String newNickname = nicknameField.getText().trim();
            if (newNickname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "昵称不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nickName = newNickname;
            nicknameField.setEditable(false);
            isEditing = false;
            saveButton.setVisible(false);
        });


        okButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(okButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createFlatButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isRollover() ? new Color(230, 230, 230) : Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(80, 32));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InformationUI().setVisible(true));
    }
}
