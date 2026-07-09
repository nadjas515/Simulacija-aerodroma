package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Input extends Dialog {

    public Input(Frame owner) {
        super(owner);
        this.setLayout(new BorderLayout(5, 5));
        Panel p = new Panel();
        p.setLayout(new GridLayout(0, 2, 5, 5));  // 2 kolone: Label + TextField

        Label name_label = new Label("Name:");
        name_label.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(name_label);
        TextField name = new TextField(20);
        name.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(name);

        Label code_label = new Label("Code:");
        code_label.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(code_label);
        TextField code = new TextField(20);
        code.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(code);

        Label x_label = new Label("X coordinate:");
        x_label.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(x_label);
        TextField x = new TextField(20);
        x.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(x);

        Label y_label = new Label("Y coordinate:");
        y_label.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(y_label);
        TextField y = new TextField(20);
        y.setFont(new Font("SansSerif", Font.PLAIN, 40));
        p.add(y);

        Button ok = new Button("OK");
        ok.setFont(new Font("SansSerif", Font.PLAIN, 40));

        this.add(p, BorderLayout.CENTER);   // Label + TextField (dve kolone)
        this.add(ok, BorderLayout.SOUTH);   // OK preko cele širine (obe kolone)
        this.setModal(true);
        this.pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        this.setVisible(true);
    }

    @Override
    public Insets getInsets() {
        Insets i = super.getInsets();   // originalne ivice (naslovna traka, okvir)
        return new Insets(i.top + 20, i.left + 20, i.bottom + 20, i.right + 20);
    }

}


class Window extends Frame {
    Window() {
        super("Airport");
        this.setSize(800, 800);
        this.setLayout(new BorderLayout());

        Button start = new Button("Input airport");
        start.setFont(new Font("SansSerif", Font.BOLD, 40));
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Input in = new Input(Window.this);
            }
        });

        Panel p = new Panel();
        p.setLayout(new FlowLayout());
        p.add(start);

        this.add(p,BorderLayout.NORTH);
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }
    public static void main(String[] args) {
        new Window();
    }
}
