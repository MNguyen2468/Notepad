//
// Name: Nguyen, Maxwell
// Date: 5/10/2024
//
// Description:
// This project recreates the Notepad application found in windows.
//

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;



public class Notepad
{
    private JTextArea ta;
    private JFileChooser chooser;
    private boolean changed;
    private JMenuItem open, save, saveAs;
    private JScrollPane textSP;

    public Notepad()
    {
        ImageIcon icon = new ImageIcon("Notepad.png");
        JFrame frame = new JFrame("Notepad");
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if(!changed)
                    System.exit(0);
                else
                {
                    int choice = JOptionPane.showConfirmDialog(frame, "Do you want to save your changes?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(choice)
                    {
                        case(JOptionPane.NO_OPTION) -> System.exit(0);
                        case(JOptionPane.YES_OPTION) -> save.doClick();
                    }
                }
            }
        });

        JMenuBar mb = new JMenuBar();
        frame.setJMenuBar(mb);

        chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Java and Text Files", "java", "txt"));
        changed = false;

        //File Menu
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        mb.add(file);

        JMenuItem newItem = new JMenuItem("New", 'N');
        newItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(event -> { // The program will prompt the user if the text has been changed when creating a new file.
            if(changed)
            {
                int value = JOptionPane.showConfirmDialog(frame, "Do you want to save your changes?", "Change Notification", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (value)
                {
                    case JOptionPane.YES_OPTION:
                        save.doClick();
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                }
            }

            chooser.setSelectedFile(null);
            ta.setText("");
            frame.setTitle("Notepad");
            changed = false;

        });
        file.add(newItem);

        open = new JMenuItem("Open...", 'O');
        open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        open.addActionListener(event -> {// The program will prompt the user if the text has been changed when opening a file.
            if(changed)
            {
                int value = JOptionPane.showConfirmDialog(frame, "Do you want to save your changes?", "Change Notification", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (value)
                {
                    case JOptionPane.YES_OPTION:
                        save.doClick();
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                }
            }

            int value = chooser.showOpenDialog(frame);
            if (value == chooser.APPROVE_OPTION)
            {
                try
                {
                    Scanner scanner = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z");
                    String text = "";
                    if(scanner.hasNext())
                        text = scanner.next();
                    ta.setText(text);
                    frame.setTitle(chooser.getSelectedFile().getName() + " - Notepad");
                    changed = false;
                } catch (FileNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
        file.add(open);

        save = new JMenuItem("Save", 'S');
        save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        save.addActionListener(event -> {
            if(chooser.getSelectedFile() == null)
            {
                saveAs.doClick();
            }else
            {
                try
                {
                    FileWriter fw = new FileWriter(chooser.getSelectedFile());
                    fw.write(ta.getText());
                    fw.close();
                    changed = false;
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
        file.add(save);

        saveAs = new JMenuItem("Save As...", 'A');
        saveAs.setDisplayedMnemonicIndex(5);
        saveAs.addActionListener(event -> {
            int value = chooser.showDialog(frame, "Save As");
            if(value == chooser.APPROVE_OPTION)
            {
                String path = chooser.getSelectedFile().getPath();

                // If the name of the file ends with not .txt or .java, attach .txt
                if (!path.substring(path.length() - 4).equals(".txt") && !path.substring(path.length() - 5).equals(".java"))
                    path = path + ".txt";

                try
                {
                    File f = new File(path);

                    // If the file does not exist, create it. If it does, then give the option of marking it as a
                    // duplicate (with numbers), overwriting or, go back to the dialog.
                    if(f.createNewFile())
                    {
                        FileWriter fw = new FileWriter(path);
                        fw.write(ta.getText());
                        fw.close();
                        chooser.setSelectedFile(f);
                        frame.setTitle(chooser.getSelectedFile().getName() + " - Notepad");
                        changed = false;
                    } else
                    {
                        int choice = JOptionPane.showConfirmDialog(chooser, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                        FileWriter fw = new FileWriter(path);
                        switch(choice)
                        {
                            case JOptionPane.YES_OPTION:
                                fw = new FileWriter(path);
                                fw.write(ta.getText());
                                fw.close();
                                chooser.setSelectedFile(f);
                                frame.setTitle(chooser.getSelectedFile().getName() + " - Notepad");
                                changed = false;
                                break;

                            case JOptionPane.NO_OPTION:
                                String suffix = "";
                                String tempPath = "";
                                int count = 2;


                                if(path.substring(path.length() - 4).equals(".txt"))
                                {
                                    suffix = ".txt";
                                    tempPath = path.substring(0,path.length() - 4);

                                } else if (path.substring(path.length() - 5).equals(".java"))
                                {
                                    suffix = ".java";
                                    tempPath = path.substring(0,path.length() - 5);
                                }

                                while(!f.createNewFile())
                                {
                                    f = new File(tempPath + " (" + count + ")" + suffix);
                                    count++;
                                }
                                fw = new FileWriter(path);
                                fw.write(ta.getText());
                                fw.close();
                                chooser.setSelectedFile(f);
                                frame.setTitle(chooser.getSelectedFile().getName() + " - Notepad");
                                changed = false;
                                break;

                            case JOptionPane.CANCEL_OPTION:
                                saveAs.doClick();
                                break;
                        }

                    }

                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
        file.add(saveAs);

        file.addSeparator();

        JMenuItem pageSetUp = new JMenuItem("Page Setup...", 'u');
        pageSetUp.setEnabled(false);
        file.add(pageSetUp);

        JMenuItem print = new JMenuItem("Print...", 'P');
        print.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.CTRL_DOWN_MASK));
        print.setEnabled(false);
        file.add(print);

        file.addSeparator();

        JMenuItem exit = new JMenuItem("Exit", 'x');
        exit.addActionListener(event -> System.exit(0));
        file.add(exit);



        // Edit Menu
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic('E');
        mb.add(edit);

        JMenuItem undo = new JMenuItem("Undo", 'U');
        undo.setEnabled(false);
        edit.add(undo);

        edit.addSeparator();

        JMenuItem cut = new JMenuItem("Cut", 't');
        cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
        cut.addActionListener(event -> ta.cut());
        edit.add(cut);

        JMenuItem copy = new JMenuItem("Copy", 'C');
        copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
        copy.addActionListener(event -> ta.copy());
        edit.add(copy);

        JMenuItem paste = new JMenuItem("Paste", 'P');
        paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
        paste.addActionListener(event -> ta.paste());
        edit.add(paste);

        JMenuItem delete = new JMenuItem("Delete", 'l');
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        delete.addActionListener(event -> ta.replaceSelection(""));
        edit.add(delete);

        edit.addSeparator();


        JMenuItem find = new JMenuItem("Find...", 'F');
        find.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK));
        find.addActionListener(event ->
        {
            Dialogs.showFindDialog(frame, ta, ta.getSelectedText());
        });
        edit.add(find);

        JMenuItem findNext = new JMenuItem("Find Next", 'N');
        findNext.setDisplayedMnemonicIndex(5);
        findNext.setEnabled(false);
        edit.add(findNext);

        JMenuItem replace = new JMenuItem("Replace...", 'R');
        replace.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_DOWN_MASK));
        replace.setEnabled(false);
        edit.add(replace);

        JMenuItem goTo = new JMenuItem("Go To...", 'G');
        goTo.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.CTRL_DOWN_MASK));
        goTo.addActionListener(event ->{
            Dialogs.showGoToDialog(frame, ta);
        });
        edit.add(goTo);

        edit.addSeparator();

        JMenuItem selectAll = new JMenuItem("Select All", 'A');
        selectAll.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));
        selectAll.addActionListener(event -> ta.selectAll());
        edit.add(selectAll);

        JMenuItem timeDate = new JMenuItem("Time/Data", 'D');
        timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        timeDate.addActionListener(event ->
        {
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:m a M/d/y");
            String text = date.format(formatter);
            ta.insert(text, ta.getCaretPosition());
        });
        edit.add(timeDate);



        // Format Menu
        JMenu format = new JMenu("Format");
        format.setMnemonic('F');
        mb.add(format);

        JCheckBoxMenuItem wordWrap = new JCheckBoxMenuItem("Word Wrap", false);
        wordWrap.setMnemonic('W');
        wordWrap.addActionListener(event -> {
            if(wordWrap.getState())
            {
                textSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                ta.setLineWrap(true);
                ta.setWrapStyleWord(true);

            }else
            {
                textSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                ta.setLineWrap(false);
                ta.setWrapStyleWord(false);

            }
        });
        format.add(wordWrap);

        JMenuItem fontItem = new JMenuItem("Font...", 'F');
        fontItem.addActionListener(event -> {
            Font font = Dialogs.showFontDialog(frame, "Font", ta.getFont());
            if(font != null)
                ta.setFont(font);
        });
        format.add(fontItem);

        JMenu color = new JMenu("Color");
        color.setMnemonic('C');
        format.add(color);

        JMenuItem background = new JMenuItem("Background...", 'B');
        background.addActionListener(event -> {
            Color colorChoice = JColorChooser.showDialog(frame, "Pick Background Color", ta.getBackground());
            if(colorChoice != null)
                ta.setBackground(colorChoice);
        });
        color.add(background);

        JMenuItem foreground = new JMenuItem("Foreground...", 'F');
        foreground.addActionListener(event -> {
            Color colorChoice = JColorChooser.showDialog(frame, "Pick Foreground Color", ta.getForeground());
            if(colorChoice != null)
                ta.setForeground(colorChoice);
        });
        color.add(foreground);



        // View Menu
        JMenu view = new JMenu("View");
        view.setMnemonic('V');
        mb.add(view);

        JMenuItem statusBar = new JMenuItem("Status Bar", 'S');
        statusBar.setEnabled(false);
        view.add(statusBar);



        // Help Menu
        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        mb.add(help);

        JMenuItem viewHelp = new JMenuItem("View Help", 'H');
        viewHelp.setEnabled(false);
        help.add(viewHelp);

        JMenuItem extraCredits = new JMenuItem("Extra Credits...", 'x');
        extraCredits.setEnabled(false);
        help.add(extraCredits);

        help.addSeparator();

        JMenuItem aboutNotepad = new JMenuItem("About Notepad", 'A');
        aboutNotepad.addActionListener(event -> JOptionPane.showMessageDialog(frame, "Created by Maxwell Nguyen.\nCompleted on 5/10/2024.", "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(aboutNotepad);


        // Text area
        ta = new JTextArea();
        ta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                changed = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                changed = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                changed = true;
            }
        });
        textSP = new JScrollPane(ta);
        frame.add(textSP);

        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new Notepad());
    }


}
