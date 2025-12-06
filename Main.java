import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new UserInterface());
    }
}

class UserInterface extends Frame implements ActionListener {

    private static final String CSV_FILE = "users.csv";
    private static final Map<String, String> users = new HashMap<>();

    private CardLayout cardLayout;
    private Panel mainPanel;
    private Panel loginPanel;
    private Panel investmentPanel;

    private TextField usernameField;
    private TextField passwordField;
    private Label loginStatusLabel;

    private TextField amountField;
    private CheckboxGroup investmentChoice;
    private Checkbox mobileCheckbox;
    private Checkbox clothesCheckbox;
    private Button startTradingButton;

    private TextArea outputArea;

    private String currentUsername = "";

    public UserInterface() {
        super("Trading Simulator");
        loadUsersFromCsv();

        cardLayout = new CardLayout();
        mainPanel = new Panel(cardLayout);

        createLoginPanel();
        createInvestmentPanel();
        createOutputArea();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(investmentPanel, "INVEST");

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(outputArea, BorderLayout.SOUTH);

        setSize(600, 520);
        setVisible(true);
        cardLayout.show(mainPanel, "LOGIN");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    private void createLoginPanel() {
        loginPanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        Label title = new Label("Trading Simulator Login", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        loginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new Label("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new TextField(20);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new Label("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginStatusLabel = new Label("Please Login or Register", Label.CENTER);
        loginStatusLabel.setForeground(Color.blue);
        loginPanel.add(loginStatusLabel, gbc);

        Panel buttonPanel = new Panel(new FlowLayout());
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);
    }

    private void createInvestmentPanel() {
        investmentPanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        Label title = new Label("Investment Details", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        investmentPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        investmentPanel.add(new Label("Amount to Invest (Integer):"), gbc);
        gbc.gridx = 1;
        amountField = new TextField("10000", 15);
        investmentPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        investmentPanel.add(new Label("Invest in:"), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        Panel radioPanel = new Panel(new FlowLayout());
        investmentChoice = new CheckboxGroup();
        mobileCheckbox = new Checkbox("Mobiles", investmentChoice, true);
        clothesCheckbox = new Checkbox("Clothes", investmentChoice, false);
        radioPanel.add(mobileCheckbox);
        radioPanel.add(clothesCheckbox);
        investmentPanel.add(radioPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        startTradingButton = new Button("Start Trading Simulation");
        startTradingButton.addActionListener(this);
        investmentPanel.add(startTradingButton, gbc);
    }

    private void createOutputArea() {
        outputArea = new TextArea("Simulation Output will appear here...\n", 15, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        outputArea.setEditable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Login")) handleLogin();
        else if (command.equals("Register")) handleRegister();
        else if (command.equals("Start Trading Simulation")) handleStartTrading();
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Username and Password cannot be empty.");
            loginStatusLabel.setForeground(Color.red);
            return;
        }

        if (!users.containsKey(username)) {
            loginStatusLabel.setText("Login failed: Username not found.");
            loginStatusLabel.setForeground(Color.red);
        } else if (!users.get(username).equals(password)) {
            loginStatusLabel.setText("Login failed: Incorrect password.");
            loginStatusLabel.setForeground(Color.red);
        } else {
            currentUsername = username;
            outputArea.setText("Welcome back " + currentUsername + "!\n");
            loginStatusLabel.setText("Login successful. Welcome back " + currentUsername + "!");
            loginStatusLabel.setForeground(Color.green);
            cardLayout.show(mainPanel, "INVEST");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Username and Password cannot be empty.");
            loginStatusLabel.setForeground(Color.red);
            return;
        }

        if (users.containsKey(username)) {
            loginStatusLabel.setText("Registration failed: Username already exists.");
            loginStatusLabel.setForeground(Color.red);
        } else {
            registerUser(username, password);
            createUserFile(username, 0);
            currentUsername = username;
            outputArea.setText("Welcome " + currentUsername + "! Registration successful.\n");
            loginStatusLabel.setText("Registration successful. Welcome " + currentUsername + "!");
            loginStatusLabel.setForeground(Color.green);
            cardLayout.show(mainPanel, "INVEST");
        }
    }

    private void handleStartTrading() {
        int amount;
        try {
            amount = Integer.parseInt(amountField.getText().trim());
            if (amount <= 0) {
                outputArea.setText("Error: Please enter a positive investment amount.\n");
                return;
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("Error: Please enter a valid integer for the investment amount.\n");
            return;
        }

        boolean investInMobiles = mobileCheckbox.getState();

        startTradingButton.setEnabled(false);
        outputArea.setText("\n--- Starting Trading Simulation ---\n");

        new Thread(() -> {
            PrintStream originalOut = System.out;
            try {
                initializeUserFileWallet(currentUsername, amount);
                TradingSystem system = new TradingSystem(currentUsername, amount, investInMobiles);

                PrintStream printStream = new PrintStream(new CustomOutputStream(outputArea));
                System.setOut(printStream);

                System.out.println("\nSimulating trading for today...");
                system.simulateDay();
                System.out.println("Trading completed check " + currentUsername + ".txt");
                System.out.println("\n--- Simulation Finished ---\n");
            } catch (Exception ex) {
                originalOut.println("An error occurred: " + ex.getMessage());
                outputArea.append("\nAn error occurred: " + ex.getMessage() + "\n");
            } finally {
                System.setOut(originalOut);
                EventQueue.invokeLater(() -> startTradingButton.setEnabled(true));
            }
        }).start();
    }

    private class CustomOutputStream extends OutputStream {
        private TextArea textArea;

        public CustomOutputStream(TextArea textArea) { this.textArea = textArea; }

        @Override
        public void write(int b) throws IOException {
            EventQueue.invokeLater(() -> {
                textArea.append(String.valueOf((char) b));
                if (b == '\n') textArea.setCaretPosition(textArea.getText().length());
            });
        }
    }

    public static void createUserFile(String username, int initialWallet) {
        try {
            File f = new File(username + ".txt");
            if (f.createNewFile()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.println("WALLET=" + initialWallet);
                    pw.println();
                    pw.println("YESTERDAY");
                    pw.println();
                    pw.println();
                    pw.println("INVENTORY");
                    pw.println("None");
                    pw.println();
                    pw.println("SOLD");
                    pw.println("None");
                    pw.println();
                    pw.println("PROFIT");
                    pw.println("total_profit=0");
                    pw.println();
                    pw.println("HISTORY");
                    pw.println();
                }
                System.out.println("Created data file: " + f.getName());
            }
        } catch (Exception e) { System.err.println("Error creating user file: " + e); }
    }

    private static void initializeUserFileWallet(String username, int initialWallet) {
        File f = new File(username + ".txt");
        if (!f.exists()) {
            createUserFile(username, initialWallet);
            return;
        }
        try {
            List<String> lines = new ArrayList<>();
            boolean walletLineFound = false;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!walletLineFound && line.startsWith("WALLET=")) {
                        walletLineFound = true;
                        String[] parts = line.split("=", 2);
                        int current = 0;
                        try { current = Integer.parseInt(parts[1].trim()); }
                        catch (Exception ex) { current = 0; }
                        if (current == 0 && initialWallet > 0) {
                            lines.add("WALLET=" + initialWallet);
                        } else {
                            lines.add(line);
                        }
                    } else {
                        lines.add(line);
                    }
                }
            }
            if (!walletLineFound) {
                lines.add(0, "WALLET=" + initialWallet);
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                for (String s : lines) pw.println(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void registerUser(String username, String password) {
        try (FileWriter fw = new FileWriter(CSV_FILE, true)) {
            fw.write(username + "," + password + "\n");
            users.put(username, password);
        } catch (Exception e) { System.err.println("Error registering user: " + e); }
    }

    public static void loadUsersFromCsv() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains(",")) continue;
                String[] arr = line.split(",", 2);
                if (arr.length == 2) users.put(arr[0].trim(), arr[1].trim());
            }
        } catch (Exception e) { System.err.println("Error loading users: " + e); }
    }
}

class TradingSystem {

    private int[] yesterday;
    private int wallet;
    private String username;
    private boolean inMobiles;
    private int totalProfit;

    public static final int ITEM_COUNT = 100;

    public TradingSystem(String username, int investment, boolean inMobiles) {
        this.username = username;
        this.inMobiles = inMobiles;

        this.wallet = loadWalletFromFile();
        if (this.wallet == 0 && investment > 0) { this.wallet = investment; persistWalletOnly(); }

        this.yesterday = loadYesterdayFromFile();
        if (this.yesterday == null) {
            PriceGenerator pg = new PriceGenerator();
            ItemGenerator ig = inMobiles ? new Mobiles() : new Clothes();
            this.yesterday = pg.generatePrices(ig.min, ig.max, ITEM_COUNT, "Initial Yesterday");
            writeYesterdayToFile(this.yesterday);
        }

        this.totalProfit = loadProfitFromFile();
    }

    public void simulateDay() {
        int[] today;
        PriceGenerator pg = new PriceGenerator();
        ItemGenerator ig = inMobiles ? new Mobiles() : new Clothes();
        today = pg.generatePrices(ig.min, ig.max, ITEM_COUNT, "Today");

        TradingNeuron neuron = new TradingNeuron();
        String[] actions = neuron.decide(yesterday, today);

        processTrades(today, actions);
        writeYesterdayToFile(today);
        yesterday = today;
    }

    private void processTrades(int[] today, String[] actions) {
        Map<Integer, Integer> inventory = new HashMap<>();
        List<String> sold = new ArrayList<>();
        List<String> history = new ArrayList<>();
        loadUserFile(inventory, sold, history);

        int initialWallet = wallet;
        int cashFlow = 0;
        List<String> boughtThisDay = new ArrayList<>();
        List<String> soldThisDay = new ArrayList<>();

        List<Integer> buyCandidates = new ArrayList<>();
        for (int i = 0; i < actions.length; i++) if ("BUY".equals(actions[i])) buyCandidates.add(i);
        buyCandidates.sort(Comparator.comparingInt(i -> today[i]));

        for (int idx : buyCandidates) {
            int price = today[idx];
            if (wallet >= price) {
                wallet -= price;
                cashFlow -= price;
                inventory.put(idx, price);
                boughtThisDay.add(idx + "=" + price);
            }
        }

        List<SellCandidate> sellCandidates = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : inventory.entrySet()) {
            int idx = e.getKey();
            int buyPrice = e.getValue();
            int sellPrice = today[idx];
            int profit = sellPrice - buyPrice;
            if (profit > 0 && "SELL".equals(actions[idx])) {
                sellCandidates.add(new SellCandidate(new InventoryItem(idx, buyPrice), sellPrice, profit));
            }
        }

        sellCandidates.sort((a, b) -> Integer.compare(b.profit, a.profit));

        for (SellCandidate sc : sellCandidates) {
            InventoryItem it = sc.item;
            int idx = it.index;
            if (inventory.containsKey(idx) && inventory.get(idx) == it.buyPrice) {
                inventory.remove(idx);
                wallet += sc.sellPrice;
                cashFlow += sc.sellPrice;
                totalProfit += sc.profit;
                String soldLine = idx + "=" + it.buyPrice + "->" + sc.sellPrice + " (profit " + sc.profit + ")";
                sold.add(soldLine);
                soldThisDay.add(soldLine);
            }
        }

        List<String> todayLog = new ArrayList<>();
        todayLog.add("------ NEW DAY ------");
        todayLog.add("WALLET BEFORE: " + initialWallet);
        todayLog.add("");

        todayLog.add("BOUGHT ITEMS:");
        if (boughtThisDay.isEmpty()) todayLog.add("None");
        else todayLog.addAll(boughtThisDay);

        todayLog.add("");
        todayLog.add("SOLD ITEMS:");
        if (soldThisDay.isEmpty()) todayLog.add("None");
        else todayLog.addAll(soldThisDay);

        todayLog.add("");
        todayLog.add("NET CASH FLOW TODAY: " + cashFlow);
        todayLog.add("TOTAL WALLET: " + wallet);
        todayLog.add("TOTAL PROFIT: " + totalProfit);
        todayLog.add("--------------------------------------");

        history.addAll(todayLog);

        writeUserFile(inventory, sold, history, totalProfit);
    }

    private int loadWalletFromFile() {
        File f = new File(username + ".txt");
        if (!f.exists()) return 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("WALLET=")) {
                    try { return Integer.parseInt(line.split("=",2)[1].trim()); }
                    catch (Exception e) { return 0; }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int loadProfitFromFile() {
        File f = new File(username + ".txt");
        if (!f.exists()) return 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean inProfit = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("PROFIT")) { inProfit = true; continue; }
                if (inProfit) {
                    if (line.startsWith("total_profit=")) {
                        try { return Integer.parseInt(line.split("=",2)[1].trim()); }
                        catch (Exception e) { return 0; }
                    }
                    if (line.equals("HISTORY")) break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private void persistWalletOnly() {
        File f = new File(username + ".txt");
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("WALLET=" + wallet);
                pw.println();
                pw.println("YESTERDAY");
                pw.println();
                pw.println("INVENTORY");
                pw.println("None");
                pw.println();
                pw.println("SOLD");
                pw.println("None");
                pw.println();
                pw.println("PROFIT");
                pw.println("total_profit=" + totalProfit);
                pw.println();
                pw.println("HISTORY");
                pw.println();
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) lines.add(line);
            }
            boolean walletFound = false;
            for (int i = 0; i < lines.size(); i++) {
                String l = lines.get(i);
                if (l.startsWith("WALLET=")) {
                    lines.set(i, "WALLET=" + wallet);
                    walletFound = true;
                    break;
                }
            }
            if (!walletFound) lines.add(0, "WALLET=" + wallet);

            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                for (String s : lines) pw.println(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadUserFile(Map<Integer, Integer> inventory, List<String> sold, List<String> history) {
        File f = new File(username + ".txt");
        if (!f.exists()) return;

        boolean inInventory = false, inSold = false, inHistory = false;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("INVENTORY")) {
                    inInventory = true; inSold = false; inHistory = false;
                    continue;
                } else if (line.equals("SOLD")) {
                    inSold = true; inInventory = false; inHistory = false;
                    continue;
                } else if (line.equals("HISTORY")) {
                    inHistory = true; inInventory = false; inSold = false;
                    continue;
                } else if (line.equals("YESTERDAY") || line.equals("PROFIT") || line.startsWith("WALLET=")) {
                    inInventory = inSold = inHistory = false;
                    continue;
                }

                if (inInventory) {
                    if (line.equals("None")) continue;
                    if (line.contains("=")) {
                        String[] p = line.split("=", 2);
                        try {
                            int idx = Integer.parseInt(p[0].trim());
                            int bp = Integer.parseInt(p[1].trim());
                            inventory.put(idx, bp);
                        } catch (Exception ignored) {}
                    }
                } else if (inSold) {
                    if (line.equals("None")) continue;
                    sold.add(line);
                } else if (inHistory) {
                    history.add(line);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String readYesterdayLineFromFile() {
        File f = new File(username + ".txt");
        if (!f.exists()) return "";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean inYesterday = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("YESTERDAY")) { inYesterday = true; continue; }
                if (inYesterday) {
                    if (line.trim().isEmpty()) return "";
                    return line.trim();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }

    private void writeUserFile(Map<Integer, Integer> inventory, List<String> sold, List<String> history, int totalProfit) {
        String yesterdayLine = readYesterdayLineFromFile();

        try (PrintWriter pw = new PrintWriter(new FileWriter(username + ".txt"))) {
            pw.println("WALLET=" + wallet);
            pw.println();
            pw.println("YESTERDAY");
            if (yesterdayLine.isEmpty()) pw.println();
            else pw.println(yesterdayLine);
            pw.println();
            pw.println("INVENTORY");
            if (inventory.isEmpty()) pw.println("None");
            else {
                for (Map.Entry<Integer, Integer> e : inventory.entrySet()) {
                    pw.println(e.getKey() + "=" + e.getValue());
                }
            }
            pw.println();
            pw.println("SOLD");
            if (sold.isEmpty()) pw.println("None");
            else {
                for (String s : sold) pw.println(s);
            }
            pw.println();
            pw.println("PROFIT");
            pw.println("total_profit=" + totalProfit);
            pw.println();
            pw.println("HISTORY");
            if (history.isEmpty()) pw.println();
            else {
                for (String s : history) pw.println(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private int[] loadYesterdayFromFile() {
        File f = new File(username + ".txt");
        if (!f.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean inYesterday = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("YESTERDAY")) { inYesterday = true; continue; }
                if (inYesterday) {
                    if (line.isEmpty()) return null;
                    String[] parts = line.split("\\s+");
                    int[] arr = new int[ITEM_COUNT];
                    for (int i = 0; i < ITEM_COUNT; i++) {
                        if (i < parts.length) {
                            try { arr[i] = Integer.parseInt(parts[i]); }
                            catch (Exception ex) { arr[i] = 0; }
                        } else arr[i] = 0;
                    }
                    return arr;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private void writeYesterdayToFile(int[] yesterdayArr) {
        File f = new File(username + ".txt");
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("WALLET=" + wallet);
                pw.println();
                pw.println("YESTERDAY");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < yesterdayArr.length; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(yesterdayArr[i]);
                }
                pw.println(sb.toString());
                pw.println();
                pw.println("INVENTORY");
                pw.println("None");
                pw.println();
                pw.println("SOLD");
                pw.println("None");
                pw.println();
                pw.println("PROFIT");
                pw.println("total_profit=" + totalProfit);
                pw.println();
                pw.println("HISTORY");
                pw.println();
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                boolean wroteYesterday = false;
                while ((line = br.readLine()) != null) {
                    if (line.trim().equals("YESTERDAY") && !wroteYesterday) {
                        lines.add("YESTERDAY");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < yesterdayArr.length; i++) {
                            if (i > 0) sb.append(" ");
                            sb.append(yesterdayArr[i]);
                        }
                        lines.add(sb.toString());
                        wroteYesterday = true;
                        continue;
                    } else {
                        lines.add(line);
                    }
                }
            }
            boolean hasYesterday = false;
            for (String s : lines) if (s.trim().equals("YESTERDAY")) { hasYesterday = true; break; }
            if (!hasYesterday) {
                List<String> newLines = new ArrayList<>();
                newLines.add("WALLET=" + wallet);
                newLines.add("");
                newLines.add("YESTERDAY");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < yesterdayArr.length; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(yesterdayArr[i]);
                }
                newLines.add(sb.toString());
                newLines.add("");
                newLines.addAll(lines);
                lines = newLines;
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                for (String s : lines) pw.println(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static class InventoryItem { int index, buyPrice; InventoryItem(int i,int b){index=i;buyPrice=b;} }
    private static class SellCandidate { InventoryItem item; int sellPrice; int profit; SellCandidate(InventoryItem it,int s,int p){this.item=it;this.sellPrice=s;this.profit=p;} }

}

class TradingNeuron {

    public String[] decide(int[] yesterday, int[] today) {
        int n = Math.min(today.length, yesterday.length);
        String[] actions = new String[n];

        List<ItemDiff> diffs = new ArrayList<>();
        for (int i = 0; i < n; i++) diffs.add(new ItemDiff(i, yesterday[i], today[i]));

        diffs.sort((a,b) -> Integer.compare(b.profit, a.profit));

        int maxActions = Math.max(1, n / 8);
        int sells = 0;

        for (ItemDiff d : diffs) {
            if (d.profit > 0 && sells < maxActions) { actions[d.index] = "SELL"; sells++; }
            else actions[d.index] = "HOLD";
        }

        List<ItemDiff> holdCandidates = new ArrayList<>();
        for (ItemDiff d : diffs) if ("HOLD".equals(actions[d.index])) holdCandidates.add(d);
        holdCandidates.sort(Comparator.comparingInt(a -> a.today));

        int buys = 0;
        for (ItemDiff d : holdCandidates) {
            if (d.yesterday > 0 && d.today <= (int)(d.yesterday * 0.90) && buys < maxActions) {
                actions[d.index] = "BUY";
                buys++;
            }
        }

        for (int i = 0; i < n; i++) if (actions[i] == null) actions[i] = "HOLD";
        return actions;
    }

    static class ItemDiff {
        int index, yesterday, today, profit;
        ItemDiff(int index, int yesterday, int today) {
            this.index = index; this.yesterday = yesterday; this.today = today;
            this.profit = today - yesterday;
        }
    }
}

class PriceGenerator {
    public int[] generatePrices(int min, int max, int size, String label) {
        int[] prices = new int[size];
        Random random = new Random();
        System.out.println("\nGenerating " + label + " prices...");
        for (int i = 0; i < size; i++) {
            int price = random.nextInt(max - min + 1) + min;
            prices[i] = price;
            try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
            System.out.println(label + " Item " + i + ":" + price);
        }
        System.out.println("\n--- " + label + " price generation completed ---");
        return prices;
    }
}

class ItemGenerator { protected int min, max, size; public ItemGenerator(int min, int max, int size){this.min=min;this.max=max;this.size=size;} }
class Mobiles extends ItemGenerator { public Mobiles(){ super(10000, 30000, TradingSystem.ITEM_COUNT); } }
class Clothes extends ItemGenerator { public Clothes(){ super(500, 5000, TradingSystem.ITEM_COUNT); } }
