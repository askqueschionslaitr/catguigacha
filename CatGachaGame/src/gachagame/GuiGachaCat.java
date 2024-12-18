package gachagame;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GuiGachaCat extends JFrame {
	
    public static void main(String[] args) {
        new GuiGachaCat().setVisible(true);
    }

    private JPanel mainMenu, summonPage, inventoryPage;
    private JLabel welcomeLabel, catImageLabel, resultLabel;
    private List<Cat> cats = new ArrayList<>();
    private Map<Cat, Integer> ownedCats = new HashMap<>(); // Tracks cats and their summon counts

    class Cat {
        String name;
        ImageIcon image;

        Cat(String name, String imagePath) {
            this.name = name;
            this.image = new ImageIcon(imagePath);
        }
    }

    class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel(String imagePath) {
            this.background = new ImageIcon("images\\background.jpg").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public GuiGachaCat() {
        setTitle("Cat Gacha Game");
        setSize(500, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Prevent immediate exit
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                showExitConfirmation(); // Show exit confirmation dialog
            }
        });
        setLayout(new CardLayout());

        loadCats();
        createMainMenu();
        createSummonPage();
        createInventoryPage();

        add(mainMenu, "MainMenu");
        add(summonPage, "SummonPage");
        add(inventoryPage, "InventoryPage");

        showPage("MainMenu");
    }

    private void showExitConfirmation() {
        // Create a custom dialog for the exit confirmation
        JDialog exitDialog = new JDialog(this, "Exit Confirmation", true);
        exitDialog.setSize(430, 320);
        exitDialog.setLayout(new BorderLayout());

        // Add an image
        JLabel imageLabel = new JLabel(new ImageIcon("images\\exitimage.jpg"), JLabel.CENTER);

        // Add a confirmation message
        JLabel messageLabel = new JLabel("Exit? 90% of gamblers quit before making it big", JLabel.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));

        // Buttons for "Yes" and "No"
        JPanel buttonPanel = new JPanel();
        JButton yesButton = new JButton("Yes");
        yesButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        yesButton.addActionListener(e -> System.exit(0)); // Exit the application

        JButton noButton = new JButton("No");
        noButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        noButton.addActionListener(e -> exitDialog.dispose()); // Close the dialog

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        // Add components to the dialog
        exitDialog.add(imageLabel, BorderLayout.CENTER);
        exitDialog.add(messageLabel, BorderLayout.NORTH);
        exitDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Center the dialog on the screen
        exitDialog.setLocationRelativeTo(this);
        exitDialog.setVisible(true);
    }

    private void showPage(String page) {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), page);
        if (page.equals("InventoryPage")) refreshInventory();
    }

    private void createMainMenu() {
        mainMenu = new BackgroundPanel("images/main_background.jpg");
        mainMenu.setLayout(null);

        welcomeLabel = new JLabel("<html><center>Welcome!<br>Test your luck<br>and summon CATS</center></html>", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        welcomeLabel.setBounds(100, 50, 300, 100);

        JButton playButton = createButton("PLAY", 175, 250, e -> showPage("SummonPage"));
        JButton inventoryButton = createButton("INVENTORY", 175, 320, e -> showPage("InventoryPage"));

        mainMenu.add(welcomeLabel);
        mainMenu.add(playButton);
        mainMenu.add(inventoryButton);
    }

    private void createSummonPage() {
        summonPage = new BackgroundPanel("images/summon_background.jpg");
        summonPage.setLayout(null);

        resultLabel = new JLabel("Click summon to get a cat!", JLabel.CENTER);
        resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        resultLabel.setBounds(100, 50, 300, 30);

        catImageLabel = new JLabel("", JLabel.CENTER);
        catImageLabel.setBounds(150, 100, 200, 200);

        JButton summonButton = createButton("SUMMON", 175, 320, e -> summonCat());
        JButton inventoryButton = createButton("INVENTORY", 175, 370, e -> showPage("InventoryPage"));
        JButton backButton = createButton("BACK", 175, 420, e -> showPage("MainMenu"));

        summonPage.add(resultLabel);
        summonPage.add(catImageLabel);
        summonPage.add(summonButton);
        summonPage.add(inventoryButton);
        summonPage.add(backButton);
    }

    private void createInventoryPage() {
        inventoryPage = new BackgroundPanel("images/inventory_background.jpg");
        inventoryPage.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("INVENTORY", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        inventoryPage.add(titleLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        gridPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(createButton("BACK", 0, 0, e -> showPage("MainMenu")));
        bottomPanel.add(createButton("RESET", 0, 0, e -> resetInventory()));

        inventoryPage.add(scrollPane, BorderLayout.CENTER);
        inventoryPage.add(bottomPanel, BorderLayout.SOUTH);
        inventoryPage.putClientProperty("gridPanel", gridPanel);
    }

    private void refreshInventory() {
        JPanel gridPanel = (JPanel) inventoryPage.getClientProperty("gridPanel");
        gridPanel.removeAll();

        for (Map.Entry<Cat, Integer> entry : ownedCats.entrySet()) {
            Cat cat = entry.getKey();
            int count = entry.getValue();

            JPanel catPanel = new JPanel(new BorderLayout());
            catPanel.setOpaque(false);

            JLabel imageLabel = new JLabel(cat.image);
            JLabel nameLabel = new JLabel(cat.name, JLabel.CENTER);
            JLabel countLabel = new JLabel("x" + count, JLabel.CENTER);

            nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
            countLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));

            catPanel.add(imageLabel, BorderLayout.CENTER);
            catPanel.add(nameLabel, BorderLayout.NORTH);
            catPanel.add(countLabel, BorderLayout.SOUTH);
            gridPanel.add(catPanel);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void resetInventory() {
        ownedCats.clear();
        refreshInventory();
    }

    private void summonCat() {
        Cat randomCat = cats.get(new Random().nextInt(cats.size()));
        ownedCats.put(randomCat, ownedCats.getOrDefault(randomCat, 0) + 1);

        catImageLabel.setIcon(randomCat.image);
        resultLabel.setText("You got: " + randomCat.name);
    }

    private JButton createButton(String text, int x, int y, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBounds(x, y, 150, 40);
        button.addActionListener(action);
        return button;
    }

    private void loadCats() {
        cats.add(new Cat("Mythical Indino Kitty", "images/cat1.png"));
        cats.add(new Cat("Photosynthesis Cat", "images/cat2.jpg"));
        cats.add(new Cat("Epic Santa Caty", "images/cat3.jpg"));
        cats.add(new Cat("Chunky Wizard Cat", "images/cat4.jpg"));
        cats.add(new Cat("Empty Brain Cat", "images/cat5.jpg"));
        cats.add(new Cat("erm actually Cat", "images/cat6.jpg"));
        cats.add(new Cat(":O Cat", "images/cat7.jpg"));
        cats.add(new Cat("Goofy ahh cartoon reaction Cat", "images/cat8.jpg"));
        cats.add(new Cat("Gorp Caty", "images/cat9.jpg"));
        cats.add(new Cat("Hapi Cat", "images/cat10.jpg"));
        cats.add(new Cat("Traumatized Cat", "images/cat11.jpg"));
        cats.add(new Cat("War Cat", "images/cat12.jpg"));
        cats.add(new Cat("Jojo Cat", "images/cat13.jpg"));
        cats.add(new Cat("BatCat", "images/cat14.jpg"));
        cats.add(new Cat("Borgir Car", "images/cat15.jpg"));
        cats.add(new Cat("KFC Delivery Kitty", "images/cat16.jpg"));
        cats.add(new Cat("Suit Gatto", "images/cat17.jpg"));
        cats.add(new Cat("hhehehehecat", "images/cat18.jpg"));
        cats.add(new Cat("U sure? Car", "images/cat19.jpg"));
        cats.add(new Cat("Stwong Black Cat", "images/cat20.jpg"));
        cats.add(new Cat("Drunk Cat", "images/cat21.jpg"));
        cats.add(new Cat("Old Cat", "images/cat22.jpg"));
        cats.add(new Cat("Handsome Gatto", "images/cat23.jpg"));
        cats.add(new Cat("Remy ahh cat", "images/cat24.jpg"));
        cats.add(new Cat("Plawer Kitty", "images/cat25.jpg"));
        cats.add(new Cat("Im ok Car", "images/cat26.jpg"));
        cats.add(new Cat("my apolocheese car", "images/cat27.jpg"));
        cats.add(new Cat("me at gym", "images/cat28.jpg"));
        cats.add(new Cat("Mr.Fresh", "images/cat29.jpg"));
        cats.add(new Cat("Weeeeeeeee Car", "images/cat30.jpg"));
        cats.add(new Cat("Ballin Cat", "images/cat31.jpg"));
        cats.add(new Cat("Stressed Cat", "images/cat32.jpg"));
        cats.add(new Cat("Kid Kitty", "images/cat33.jpg"));
        cats.add(new Cat("Gojo Car", "images/cat34.jpg"));
        cats.add(new Cat("Sushi Pusi", "images/cat35.jpg"));
        cats.add(new Cat("Buffed Kitty", "images/cat36.jpg"));
        cats.add(new Cat("Scared Cat", "images/cat37.jpg"));
        cats.add(new Cat("Cat open mouth cat inside mouth", "images/cat38.jpg"));
        cats.add(new Cat("Alawakbar Car", "images/cat39.jpg"));
        cats.add(new Cat("Cat Egg", "images/cat40.jpg"));
    }
}
