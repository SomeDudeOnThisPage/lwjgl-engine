package engine.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionDialog extends JDialog
{
  private int dialogWidth = 400;
  private int dialogHeight = 200;

  private JLabel iconLabel = new JLabel();

  // is error panel opened up
  private boolean open = false;

  private JLabel errorLabel = new JLabel();
  private JTextArea errorTextArea = new JTextArea("");

  private JTextArea exceptionTextArea = new JTextArea("");
  private JScrollPane exceptionTextAreaSP = new JScrollPane();

  private JButton okButton = new JButton("OK");
  private JButton viewButton = new JButton("View Error");
  private JButton emailButton = new JButton("Email Error");

  private JPanel topPanel = new JPanel(new BorderLayout());

  public ExceptionDialog(String errorLabelText, String errorDescription, Throwable e)
  {
    e.printStackTrace();

    StringWriter errors = new StringWriter();
    e.printStackTrace(new PrintWriter(errors));

    setSize(dialogWidth, dialogHeight);

    setResizable(false);

    errorTextArea.setText(errorDescription);

    errorLabel.setText(errorLabelText);

    exceptionTextArea.setText(errors.toString());

    exceptionTextAreaSP = new JScrollPane(exceptionTextArea);

    iconLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

    iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
    setupUI();

    setUpListeners();
  }

  public void setupUI()
  {
    this.setTitle("Error");

    errorTextArea.setLineWrap(true);
    errorTextArea.setWrapStyleWord(true);
    errorTextArea.setEditable(false);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

    buttonPanel.add(okButton);
    buttonPanel.add(viewButton);

    errorTextArea.setBackground(iconLabel.getBackground());

    JScrollPane textAreaSP = new JScrollPane(errorTextArea);

    textAreaSP.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

    errorLabel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

    exceptionTextArea.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
    exceptionTextArea.setPreferredSize(new Dimension(100, 100));

    topPanel.add(iconLabel, BorderLayout.WEST);

    JPanel p = new JPanel(new BorderLayout());
    p.add(errorLabel, BorderLayout.NORTH);
    p.add(textAreaSP);

    topPanel.add(p);
    topPanel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
    this.add(topPanel);

    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  private void setUpListeners()
  {
    okButton.addActionListener(e -> {
      ExceptionDialog.this.setVisible(false);
      System.exit(-1);
    });
    viewButton.addActionListener(e ->
    {

      if (open)
      {
        viewButton.setText("View Error");

        topPanel.remove(exceptionTextAreaSP);

        ExceptionDialog.this.setSize(dialogWidth, dialogHeight);

        topPanel.revalidate();

        open = false;

      }
      else
      {
        viewButton.setText("Hide Error");

        topPanel.add(exceptionTextAreaSP, BorderLayout.SOUTH);

        ExceptionDialog.this.setSize(dialogWidth,
          dialogHeight + 100);

        topPanel.revalidate();

        open = true;
      }
    });
  }
}