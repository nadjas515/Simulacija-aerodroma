package gui;

import data.Airport;
import exceptions.AirportExists;
import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

class ErrorDialog extends Dialog {
	ErrorDialog(java.awt.Window owner, String msg) {
		super(owner);
        Label title = new Label(msg);
        title.setFont(new Font("Verdana", Font.BOLD, 30));
        title.setForeground(Color.RED);
        title.setAlignment(Label.CENTER);
        this.add(title);
        this.setSize(800,200);
        this.setModal(true);
        this.setLocationRelativeTo(owner);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setVisible(true);
	}
}

class Input extends Dialog {

    public Input(Frame owner) {
        super(owner);
        this.setLayout(new BorderLayout(5, 5));
        Panel p = new Panel();
        p.setLayout(new GridLayout(0, 2, 50, 10));  // 2 kolone: Label + TextField

        Label name_label = new Label("Name:");
        name_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        name_label.setAlignment(Label.RIGHT);
        p.add(name_label);
        TextField name = new TextField(20);
        name.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(name);

        Label code_label = new Label("Code:");
        code_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        code_label.setAlignment(Label.RIGHT);
        p.add(code_label);
        TextField code = new TextField(20);
        code.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(code);

        Label x_label = new Label("X coordinate:");
        x_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        x_label.setAlignment(Label.RIGHT);
        p.add(x_label);
        TextField x = new TextField(20);
        x.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(x);

        Label y_label = new Label("Y coordinate:");
        y_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        y_label.setAlignment(Label.RIGHT);
        p.add(y_label);
        TextField y = new TextField(20);
        y.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(y);

        Panel button_panel = new Panel();
        button_panel.setLayout(new FlowLayout());
        Button ok = new Button("OK");
        ok.setFont(new Font("Verdana", Font.PLAIN, 40));

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameVal = name.getText();
                String codeVal = code.getText();
                String xVal    = x.getText();
                String yVal    = y.getText();

                try {
                    if (nameVal.isEmpty() || codeVal.isEmpty() || xVal.isEmpty() || yVal.isEmpty()) {
                        throw new FormatException("Sva polja trebaju biti popunjena");
                    }
                    if(codeVal.length()!=3 || codeVal.equals(codeVal.toUpperCase())){
                        throw new FormatException("Kod treba bude tačno 3 velika slova");
                    }
                    int xi = Integer.parseInt(xVal.trim());
                    int yi = Integer.parseInt(yVal.trim());
                    if(Math.abs(xi)>180)
                        throw new FormatException("X koordinata mora biti između -180 i 180");
                    if(Math.abs(yi)>90)
                        throw new FormatException("Y koordinata mora biti između -90 i 90");

                    Airport.addAirport(xi, yi, nameVal, codeVal);
                    Airport.printAirports();
                    dispose();
                }
                catch (NumberFormatException ex) {
                    new ErrorDialog(Input.this,"Brojevi nisu u dobrom formatu");
                }
                catch (AirportExists | FormatException ex) {
                    new ErrorDialog(Input.this,ex.getMessage());
                }
            }
        });

        button_panel.add(ok);

        this.add(p, BorderLayout.CENTER);   // Label + TextField (dve kolone)
        this.add(button_panel, BorderLayout.SOUTH);   // OK preko cele širine (obe kolone)
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
        return new Insets(i.top + 50, i.left + 60, i.bottom + 50, i.right + 60);
    }

}


class Window extends Frame {
    Window() {
        super("Airport");
        this.setSize(800, 800);
        this.setLayout(new BorderLayout());

        Button start = new Button("Input airport");
        start.setFont(new Font("Verdana", Font.BOLD, 40));
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
