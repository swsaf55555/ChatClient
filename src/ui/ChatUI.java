package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ChatUI extends JFrame {
    private String currentContactName = null;
    private DefaultListModel<Contact> contactsModel;
    private JList<Contact> contactsList;
    private JPanel chatPanel;
    private JPanel chatContentPanel;
    private JTextArea inputArea;
    private JScrollPane chatScrollPane; // 新增变量：用于滚动到底部
    private long lastMessageTime = 0; // 用于控制时间戳显示

    public ChatUI() {
        setTitle("聊天系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        try{
            ImageIcon icon = new ImageIcon(getClass().getResource("/default_2.jpg"));
            setIconImage(icon.getImage());
        }catch(Exception e){
            System.out.println("加载ico失败");
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("操作");
        JMenuItem myInformation = new JMenuItem("我的个人信息");
        JMenuItem addFriend = new JMenuItem("添加联系人");
        JMenuItem logout=new JMenuItem("退出登录");

        addFriend.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "请输入对方用户名：");
            if (username == null || username.trim().isEmpty()) return;

            String remark = JOptionPane.showInputDialog(this, "请输入备注名：");
            if (remark == null || remark.trim().isEmpty()) return;

            addContactToList(username.trim(), remark.trim());
        });

        myInformation.addActionListener(e -> InformationUI.main(null));
        menu.add(myInformation);
        menu.add(addFriend);
        menu.add(logout);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        contactsModel = new DefaultListModel<>();
        contactsModel.addElement(new Contact("Alice", loadAvatar("default_1.jpg"), 2));
        contactsModel.addElement(new Contact("Bob", loadAvatar("default_1.jpg"), 0));
        contactsModel.addElement(new Contact("Charlie", loadAvatar("default_2.jpg"), 5));
        for (int i = 4; i <= 20; i++) {
            contactsModel.addElement(new Contact("联系人" + i, loadAvatar("default_2.jpg"), (i % 3 == 0) ? 1 : 0));
        }

        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setCellRenderer(new ListCellRenderer<Contact>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Contact> list, Contact contact, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout(5, 5));
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                panel.setOpaque(true);

                JLabel avatarLabel;
                if (contact.unreadCount > 0) {
                    avatarLabel = new JLabel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            g.drawImage(contact.avatar.getImage(), 0, 0, null);
                            g.setColor(Color.RED);
                            g.fillOval(28, 2, 10, 10);
                        }

                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(40, 40);
                        }
                    };
                } else {
                    // 如果没有未读数
                    avatarLabel = new JLabel();
                    avatarLabel.setIcon(contact.avatar);
                    avatarLabel.setPreferredSize(new Dimension(40, 40));
                    avatarLabel.setOpaque(true);
                    avatarLabel.setBackground(new Color(245, 245, 245)); // 可选，为了更明显

                }

                JLabel nameLabel = new JLabel(contact.name);
                nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

                panel.add(avatarLabel, BorderLayout.WEST);
                panel.add(nameLabel, BorderLayout.CENTER);

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    panel.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    panel.setForeground(list.getForeground());
                }

                return panel;
            }
        });

        JScrollPane contactScroll = new JScrollPane(contactsList);
        contactScroll.setPreferredSize(new Dimension(200, 0));

        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.white);
        updateChatPanel(null);

        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Contact selected = contactsList.getSelectedValue();
                if (selected != null) {
                    selected.unreadCount = 0;
                    updateChatPanel(selected.name);
                    contactsList.repaint();
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contactScroll, chatPanel);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        getContentPane().add(splitPane);
    }
    private void addContactToList(String username, String remark) {
        // 检查是否已存在该联系人
        for (int i = 0; i < contactsModel.size(); i++) {
            Contact c = contactsModel.getElementAt(i);
            if (c.name.equals(remark)) {
                JOptionPane.showMessageDialog(this, "该备注名已存在联系人列表中！");
                return;
            }
        }

        // 添加联系人（默认头像 & 未读数为 0）
        ImageIcon avatar = loadAvatar("default_2.jpg");
        Contact newContact = new Contact(remark, avatar, 0);
        contactsModel.addElement(newContact);

        JOptionPane.showMessageDialog(this, "成功添加联系人：" + remark);
    }

    private void updateChatPanel(String contactName) {
        currentContactName = contactName;
        chatPanel.removeAll();

        if (contactName == null) {
            chatPanel.add(new JLabel("请从左侧选择联系人开始聊天", JLabel.CENTER), BorderLayout.CENTER);
        } else {
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setBackground(Color.white);
            chatContentPanel = new JPanel();
            chatContentPanel.setBackground(Color.white);
            chatContentPanel.setLayout(new BoxLayout(chatContentPanel, BoxLayout.Y_AXIS));
            chatScrollPane = new JScrollPane(chatContentPanel); // 保存到实例变量

            inputArea = new JTextArea(4, 20);
            inputArea.setLineWrap(true);
            JScrollPane inputScroll = new JScrollPane(inputArea);
            inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            // Ctrl + Enter 发送
            InputMap im = inputArea.getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap am = inputArea.getActionMap();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "sendMessage");
            am.put("sendMessage", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage(contactName);
                }
            });

            JButton sendButton =  new JButton("发送") {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(getModel().isRollover() ? new Color(230, 230, 230) : Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.BLACK);
                    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    super.paintComponent(g);
                }
            };
            sendButton.setBackground(Color.white);
            sendButton.setFocusPainted(false);
            sendButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            sendButton.setContentAreaFilled(false);
            sendButton.setOpaque(true);
            sendButton.addActionListener(e -> sendMessage(contactName));

            JPanel inputPanel = new JPanel(new BorderLayout());
            inputPanel.add(inputScroll, BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);

            messagePanel.add(chatScrollPane, BorderLayout.CENTER);
            messagePanel.add(inputPanel, BorderLayout.SOUTH);

            chatPanel.add(new JLabel("与 " + contactName + " 聊天", JLabel.CENTER), BorderLayout.NORTH);
            chatPanel.add(messagePanel, BorderLayout.CENTER);
        }

        chatPanel.revalidate();
        chatPanel.repaint();
        lastMessageTime = 0; // 切换联系人时重置时间戳记录

    }

    private void sendMessage(String contactName) {
        String text = inputArea.getText().trim();
        if (!text.isEmpty()) {
            addMessageBubble("我", text, true);
            inputArea.setText("");
            // 滚动到底部
            if (chatScrollPane != null) {
                JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
                SwingUtilities.invokeLater(() -> vertical.setValue(vertical.getMaximum()+100));
            }
        }
    }

    private void addMessageBubble(String sender, String message, boolean isSender) {
        long currentTime = System.currentTimeMillis();

        // 如果与上一条消息相隔超过1分钟，显示时间戳
        if (currentTime - lastMessageTime >= 60_000) {
            JLabel timeLabel = new JLabel(getCurrentTime(currentTime));
            timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            timeLabel.setForeground(Color.GRAY);
            timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中显示
            chatContentPanel.add(timeLabel);
            lastMessageTime = currentTime; // 更新时间戳
        }

        // 消息气泡（动态宽度）
        JTextArea msgLabel = new JTextArea(message);
        msgLabel.setWrapStyleWord(true);
        msgLabel.setLineWrap(true);
        msgLabel.setEditable(false);
        msgLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        msgLabel.setBackground(isSender ? new Color(179, 229, 252) : new Color(200, 230, 201));
        msgLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // 计算最大宽度为聊天面板宽度的90%
        int panelWidth = chatContentPanel.getWidth();
        if (panelWidth <= 0) {
            panelWidth = 300; // 还没布局好时，给个默认宽度避免出错
        }
        int maxWidth = (int)(panelWidth * 0.9);
        int minWidth = (int)(panelWidth * 0.05);

        FontMetrics fm = msgLabel.getFontMetrics(msgLabel.getFont());
        // 计算整段文字宽度（像素）
        int textPixelWidth = fm.stringWidth(message);

        // 给文字宽度加点内边距，左右各加一个汉字长度
        int padding = fm.stringWidth("我") * 2;

        // 计算最终气泡宽度，限制在最大和最小范围内
        int textWidth = Math.min(maxWidth, Math.max(minWidth, textPixelWidth + padding));

        // 设置 JTextArea 的最大和首选尺寸
        msgLabel.setMaximumSize(new Dimension(textWidth, Integer.MAX_VALUE));
        msgLabel.setPreferredSize(new Dimension(textWidth, msgLabel.getPreferredSize().height));



        JPanel bubble = new JPanel(new BorderLayout());
        bubble.add(msgLabel, BorderLayout.CENTER);
        bubble.setBackground(Color.white);
        JPanel container = new JPanel(new FlowLayout(isSender ? FlowLayout.RIGHT : FlowLayout.LEFT));
        container.setBackground(Color.white);
        container.add(bubble);

        chatContentPanel.add(container);
        chatContentPanel.revalidate();

//        // 滚动到底部
//        if (chatScrollPane != null) {
//            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
//            SwingUtilities.invokeLater(() -> vertical.setValue(vertical.getMaximum()));
//        }


        // 判断是否已经在底部
        if (chatScrollPane != null) {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            int value = vertical.getValue();
            int extent = vertical.getModel().getExtent();
            int max = vertical.getMaximum();

            boolean isAtBottom = (value + extent) >= (max - 20); // 容差设置为20像素

            if (isAtBottom) {
                // 在底部则滚动到底
                SwingUtilities.invokeLater(() -> vertical.setValue(value+extent+50));
            }
        }

    }


    private ImageIcon loadAvatar(String path) {
        try {
            java.net.URL url = getClass().getResource("/" + path);
            System.out.println("加载图片路径：" + url);
            if (url == null) throw new Exception("图片未找到: " + path);
            ImageIcon raw = new ImageIcon(url);
            Image img = raw.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            BufferedImage circle = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = circle.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 40, 40));
            g2.drawImage(img, 0, 0, null);
            g2.dispose();

            //return new ImageIcon(circle);
            return new ImageIcon(img); // 不裁剪
        } catch (Exception e) {
            System.err.println("头像加载失败: " + e.getMessage());
            return new ImageIcon(); // 返回空图标
        }
    }


    public void simulateReceiveMessage(String fromName, String message) {
        if (fromName.equals(currentContactName)) {
            addMessageBubble(fromName, message, false);
        } else {
            for (int i = 0; i < contactsModel.getSize(); i++) {
                Contact c = contactsModel.getElementAt(i);
                if (c.name.equals(fromName)) {
                    c.unreadCount++;
                    break;
                }
            }
            contactsList.repaint();
        }
    }
    private String getCurrentTime(long millis) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(new java.util.Date(millis));
    }



    class Contact {
        String name;
        ImageIcon avatar;
        int unreadCount;

        public Contact(String name, ImageIcon avatar, int unreadCount) {
            this.name = name;
            this.avatar = avatar;
            this.unreadCount = unreadCount;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatUI ui = new ChatUI();
            ui.setVisible(true);

            // 模拟 3 秒后收到一条消息
            new Timer(1000, e -> ui.simulateReceiveMessage("Alice", "你好这是一条模拟消息！")).start();
            new Timer(1000, e -> ui.simulateReceiveMessage("联系人4", "你好这是一条模拟消息！")).start();
        });
    }
}
