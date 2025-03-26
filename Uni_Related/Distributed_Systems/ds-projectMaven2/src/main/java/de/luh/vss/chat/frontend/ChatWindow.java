package de.luh.vss.chat.frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChatWindow extends JFrame {

    public interface OnSendMessageListener {
        void onSendMessage(String message);
    }

    private OnSendMessageListener sendMessageListener;

    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    public ChatWindow() {
        super("My Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        // Panel that holds all message bubbles
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        // Scroll pane around the chat panel
        scrollPane = new JScrollPane(
                chatPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Input area at the bottom
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // When user clicks "Send," notify our callback
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sendMessageListener != null) {
                    String text = inputField.getText().trim();
                    if (!text.isEmpty()) {
                        sendMessageListener.onSendMessage(text);
                        inputField.setText("");
                    }
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Let ChatClient attach a listener to handle 'Send' clicks.
     */
    public void setOnSendMessageListener(OnSendMessageListener listener) {
        this.sendMessageListener = listener;
    }

    /**
     * Add a new message bubble. Blue if fromUser = true; Gray if fromUser = false.
     */
    public void addMessageBubble(String message, boolean fromUser) {
        JPanel bubblePanel = new JPanel(new BorderLayout());
        bubblePanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel bubbleLabel = new JLabel("<html><p style='width:200px;'>" + message + "</p></html>");
        bubbleLabel.setOpaque(true);
        bubbleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (fromUser) {
            // Light-ish blue
            bubbleLabel.setBackground(new Color(173, 216, 230));
            bubbleLabel.setForeground(Color.BLACK);
            // Align to the right
            bubblePanel.add(bubbleLabel, BorderLayout.EAST);
        } else {
            // Gray
            bubbleLabel.setBackground(Color.LIGHT_GRAY);
            bubbleLabel.setForeground(Color.BLACK);
            // Align to the left
            bubblePanel.add(bubbleLabel, BorderLayout.WEST);
        }

        chatPanel.add(bubblePanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}
