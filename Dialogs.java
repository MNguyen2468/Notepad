//
// Name: Nguyen, Maxwell
// Date: 5/10/2024
//
// Description:
// This part of the project contains dialogs for font, go to, and find.
//
import javax.swing.*;
import java.awt.*;

public class Dialogs extends JFileChooser
{
    private static Font result; // The Font selector dialog for size, font, and style.
    public static Font showFontDialog(JFrame parent, String name, Font initialFont)
    {

        JDialog dialog = new JDialog(parent, name, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());


        //East, Size List
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setMinimumSize(new Dimension(100, 0));
        dialog.add(sizePanel, BorderLayout.NORTH);

        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setDisplayedMnemonic('S');
        sizePanel.add(sizeLabel, BorderLayout.NORTH);

        Integer[] sizes = {8,9,10,11,12,14,16,18,20,22,24,26,28,36,48,72};
        JComboBox<Integer> sizeCB = new JComboBox<>(sizes);
        sizeCB.setSelectedItem(initialFont.getSize());
        sizeCB.setEditable(true);
        sizeCB.getEditor().setItem(initialFont.getSize());
        sizeLabel.setLabelFor(sizeCB);
        sizePanel.add(sizeCB);


        //West, Fonts
        JPanel fontPanel = new JPanel(new BorderLayout());
        dialog.add(fontPanel, BorderLayout.WEST);

        JLabel fontLabel = new JLabel("Fonts:");
        fontLabel.setDisplayedMnemonic('F');
        fontPanel.add(fontLabel, BorderLayout.NORTH);

        JList<String> fontList = new JList<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        JScrollPane fontScroll = new JScrollPane(fontList);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.setSelectedValue(initialFont.getName(), true);
        fontList.ensureIndexIsVisible(fontList.getSelectedIndex());
        fontLabel.setLabelFor(fontScroll);
        fontPanel.add(fontScroll);


        //Center, Style
        JPanel stylePanel = new JPanel(new BorderLayout());
        dialog.add(stylePanel, BorderLayout.CENTER);

        JLabel styleLabel = new JLabel("Style:");
        stylePanel.add(styleLabel, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1));
        stylePanel.add(buttons);
        ButtonGroup styleGroup = new ButtonGroup();

        JRadioButton regular = new JRadioButton("Regular");
        regular.setMnemonic('R');
        regular.setActionCommand("0");
        styleGroup.add(regular);
        buttons.add(regular);
        JRadioButton italic = new JRadioButton("Italic");
        italic.setMnemonic('I');
        italic.setActionCommand("2");
        styleGroup.add(italic);
        buttons.add(italic);
        JRadioButton bold = new JRadioButton("Bold");
        bold.setMnemonic('B');
        bold.setActionCommand("1");
        styleGroup.add(bold);
        buttons.add(bold);

        switch (initialFont.getStyle())
        {
            case Font.PLAIN -> regular.setSelected(true);
            case Font.BOLD -> bold.setSelected(true);
            case Font.ITALIC -> italic.setSelected(true);
        }


        //South, Ok and Cancel
        JPanel okAndCancelPanel = new JPanel(new GridLayout(0,2));
        dialog.add(okAndCancelPanel, BorderLayout.SOUTH);

        JButton ok = new JButton("Ok");
        ok.setMnemonic('O');
        ok.addActionListener(event -> {
            try
            {
                int sizeValue = Integer.parseInt("" + sizeCB.getEditor().getItem());
                if(sizeValue <= 0)
                    throw new NumberFormatException();
                result = new Font(fontList.getSelectedValue(), Integer.parseInt(styleGroup.getSelection().getActionCommand()), sizeValue);
                dialog.dispose();
            }catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(parent, "Invalid input for font", "Notepad - Font", JOptionPane.ERROR_MESSAGE);
            }
        });
        okAndCancelPanel.add(ok);
        dialog.getRootPane().setDefaultButton(ok);

        JButton cancel = new JButton("Cancel");
        cancel.setMnemonic('C');
        cancel.addActionListener(event -> {
            result = null;
            dialog.dispose();
        });
        okAndCancelPanel.add(cancel);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result;
    }

    // A dialog for finding text within the page
    public static void showFindDialog(JFrame parent, JTextArea ta, String highlighted)
    {
        JDialog findDialog = new JDialog(parent, "Find", false);

        findDialog.setLayout(new BorderLayout());
        findDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel findPanel = new JPanel(new BorderLayout());
        findDialog.add(findPanel);
        findPanel.add(new JLabel("Find what: "), BorderLayout.CENTER);
        JTextField tf = new JTextField(20);
        tf.setText(highlighted == null ? "" : highlighted);
        tf.setCaretPosition(0);
        tf.setSelectionEnd(highlighted == null ? 0 : highlighted.length());
        findPanel.add(tf, BorderLayout.EAST);

        JPanel startButtons = new JPanel();
        findDialog.add(startButtons, BorderLayout.SOUTH);

        JCheckBox caseCheck = new JCheckBox("Match case");
        caseCheck.setDisplayedMnemonicIndex(6);
        startButtons.add(caseCheck);

        startButtons.add(new JLabel("Starting search location:"));

        ButtonGroup startGroup = new ButtonGroup();
        JRadioButton up = new JRadioButton("Up");
        up.setMnemonic('U');
        startGroup.add(up);
        startButtons.add(up);
        JRadioButton down = new JRadioButton("Down", true);
        down.setMnemonic('D');
        startGroup.add(down);
        startButtons.add(down);

        JPanel buttonPanel = new JPanel();
        findDialog.add(buttonPanel, BorderLayout.EAST);

        JButton find = new JButton("Find Next");
        find.setMnemonic('F');
        findDialog.getRootPane().setDefaultButton(find);
        find.addActionListener(event -> {
            String textArea = !caseCheck.isSelected() ? ta.getText().toLowerCase() : ta.getText();
            String textField = !caseCheck.isSelected() ? tf.getText().toLowerCase() : tf.getText();
            if(textField.isEmpty())
                return;
            int start = ta.getCaretPosition();

            if(down.isSelected())
            {
                int location = textArea.indexOf(textField, start);
                if (location > -1)
                {
                    ta.setCaretPosition(location);
                    ta.moveCaretPosition(location + textField.length());
                } else
                    JOptionPane.showMessageDialog(findDialog, "Cannot find \"" + tf.getText() + "\"", "Notepad - Find", JOptionPane.ERROR_MESSAGE);
            }else
            {
                int location = textArea.lastIndexOf(textField, start - 1);
                if (location > -1)
                {
                    ta.setCaretPosition(location + textField.length());
                    ta.moveCaretPosition(location);
                } else
                    JOptionPane.showMessageDialog(findDialog, "Cannot find \"" + tf.getText() + "\"", "Notepad - Find", JOptionPane.ERROR_MESSAGE);
            }

        });
        buttonPanel.add(find);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(event -> {
            findDialog.dispose();
        });
        buttonPanel.add(cancel);




        findDialog.pack();
        findDialog.setResizable(false);
        findDialog.setLocationRelativeTo(parent);
        findDialog.setVisible(true);
    }

    // A dialog for jumping to a specifc line number
    public static void showGoToDialog(JFrame parent, JTextArea ta)
    {
        JDialog goToDialog = new JDialog(parent, "Go To Line", true);
        goToDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        goToDialog.setLayout(new BorderLayout());

        goToDialog.add(new JLabel("Line number:"), BorderLayout.NORTH);

        JTextField tf = new JTextField(20);
        goToDialog.add(tf);
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        goToDialog.add(bottomButtons, BorderLayout.SOUTH);
        JButton goToB = new JButton("Go To");
        goToDialog.getRootPane().setDefaultButton(goToB);
        goToB.addActionListener(event -> {
            String textArea = ta.getText();
            int column = 0;
            try
            {
                int location = Integer.parseInt(tf.getText());

                if(ta.getLineCount() >= location && location > 0)
                {
                    ta.setCaretPosition(0);
                    if(location > 1)
                    {
                        for (int i = 1; i < location; i++)
                        {
                            column = textArea.indexOf('\n', ta.getCaretPosition()) + 1;
                            ta.setCaretPosition(column);
                        }
                    }
                    goToDialog.dispose();
                } else
                {
                    JOptionPane.showMessageDialog(parent, "The line number is beyond the total number of lines", "Notepad - Go To", JOptionPane.ERROR_MESSAGE);
                }
            }catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(parent, "Invalid input", "Notepad - Go To", JOptionPane.ERROR_MESSAGE);
            }






        });
        bottomButtons.add(goToB);
        JButton cancel = new JButton("Cancel");
        bottomButtons.add(cancel);

        goToDialog.pack();
        goToDialog.setResizable(false);
        goToDialog.setLocationRelativeTo(parent);
        goToDialog.setVisible(true);


    }



}
