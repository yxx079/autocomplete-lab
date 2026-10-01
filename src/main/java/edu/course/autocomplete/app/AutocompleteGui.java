package edu.course.autocomplete.app;

import edu.course.autocomplete.io.DatasetLoader;
import edu.course.autocomplete.benchmark.BenchmarkRunner;
import edu.course.autocomplete.search.AutocompleteEngine;
import edu.course.autocomplete.model.Term;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Autocomplete 的桌面图形界面。
 *
 * <p>界面只依赖 {@link AutocompleteEngine}，因此三周课程可以复用同一份 GUI，
 * 每周只需要调整 {@link EngineFactory} 提供的检索实现。
 */
public final class AutocompleteGui {
    private static final int DEFAULT_LIMIT = 10;
    private static final Path DEFAULT_DATASET = Path.of("data/classroom/tiny.txt");

    private AutocompleteEngine engine;
    private int recordCount;
    private String engineName;
    private Path currentDataset;
    private List<Term> currentTerms;
    private final JComboBox<String> engineSelector = new JComboBox<>();
    private JFrame frame;
    private final JComboBox<String> datasetSelector = new JComboBox<>();
    private final JButton openDatasetButton = new JButton("打开数据文件…");
    private final JButton benchmarkButton = new JButton("性能对比");
    private boolean updatingSelector;
    private boolean loading;
    private final int limit;
    private final JTextField prefixField = new JTextField();
    private final DefaultTableModel resultModel = new DefaultTableModel(
            new Object[] {"查询文本", "权重"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel statusLabel = new JLabel("请输入查询前缀");

    private AutocompleteGui(AutocompleteEngine engine, List<Term> terms, int limit) {
        this.engine = engine;
        this.engineName = engine.name();
        this.currentTerms = terms;
        this.recordCount = terms.size();
        this.limit = limit;
    }

    /** 无参数时使用 tiny.txt、linear 和 10 条结果；也可用参数指定初始配置。 */
    public static void main(String[] args) throws Exception {
        LaunchOptions options = parseArguments(args);
        Path dataset = resolveDataset(options.dataset());
        List<Term> terms = DatasetLoader.load(dataset);
        AutocompleteEngine engine = EngineFactory.create(options.engineName(), terms);

        AutocompleteGui gui = new AutocompleteGui(engine, terms, options.limit());
        SwingUtilities.invokeLater(new ShowWindowTask(gui, dataset));
    }

    static LaunchOptions parseArguments(String[] args) {
        if (args != null && args.length == 0) {
            return new LaunchOptions(DEFAULT_DATASET, "linear", DEFAULT_LIMIT);
        }
        if (args == null || args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException(
                    "可直接无参数运行，或提供：<数据文件> <检索实现> [结果数量]");
        }
        int limit = args.length == 3 ? parsePositiveLimit(args[2]) : DEFAULT_LIMIT;
        return new LaunchOptions(Path.of(args[0]), args[1].toLowerCase(Locale.ROOT), limit);
    }

    private static int parsePositiveLimit(String value) {
        try {
            int limit = Integer.parseInt(value);
            if (limit <= 0) {
                throw new IllegalArgumentException("结果数量必须是正整数");
            }
            return limit;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("结果数量必须是正整数：" + value, exception);
        }
    }

    private void showWindow(Path dataset) {
        this.frame = new JFrame("Autocomplete — " + engine.name());
        this.currentDataset = dataset.toAbsolutePath().normalize();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(12, 12));

        JLabel inputLabel = new JLabel("查询前缀：");
        inputLabel.setLabelFor(prefixField);
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(prefixField, BorderLayout.CENTER);
        inputPanel.add(benchmarkButton, BorderLayout.EAST);
        benchmarkButton.addActionListener(event -> comparePerformance());

        JPanel datasetPanel = new JPanel(new BorderLayout(8, 0));
        datasetPanel.add(new JLabel("数据文件："), BorderLayout.WEST);
        datasetPanel.add(datasetSelector, BorderLayout.CENTER);
        JPanel selectionPanel = new JPanel(new BorderLayout(8, 0));
        selectionPanel.add(openDatasetButton, BorderLayout.WEST);
        JPanel algorithmPanel = new JPanel(new BorderLayout(6, 0));
        algorithmPanel.add(new JLabel("算法："), BorderLayout.WEST);
        algorithmPanel.add(engineSelector, BorderLayout.CENTER);
        selectionPanel.add(algorithmPanel, BorderLayout.EAST);
        datasetPanel.add(selectionPanel, BorderLayout.EAST);
        for (String name : EngineFactory.supportedEngineNames()) {
            engineSelector.addItem(name);
        }
        engineSelector.setSelectedItem(engineName);
        engineSelector.addActionListener(event -> {
            if (!updatingSelector && !loading && engineSelector.getSelectedItem() != null) {
                String selected = (String) engineSelector.getSelectedItem();
                if (!selected.equals(engineName)) {
                    switchConfiguration(currentDataset, selected);
                }
            }
        });
        JPanel controls = new JPanel(new BorderLayout(0, 8));
        controls.add(datasetPanel, BorderLayout.NORTH);
        controls.add(inputPanel, BorderLayout.SOUTH);
        updateDatasetSelector();
        datasetSelector.addActionListener(event -> {
            if (!updatingSelector && !loading && datasetSelector.getSelectedItem() != null) {
                Path selected = currentDataset.getParent().resolve(
                        (String) datasetSelector.getSelectedItem());
                if (!selected.equals(currentDataset)) {
                    switchDataset(selected);
                }
            }
        });
        openDatasetButton.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser(currentDataset.getParent().toFile());
            chooser.setDialogTitle("选择 Autocomplete 数据文件");
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                switchDataset(chooser.getSelectedFile().toPath());
            }
        });

        JTable resultTable = new JTable(resultModel);
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(24);
        resultTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        resultTable.getColumnModel().getColumn(1).setMaxWidth(180);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(controls, BorderLayout.NORTH);
        content.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        content.add(statusLabel, BorderLayout.SOUTH);
        frame.add(content, BorderLayout.CENTER);

        prefixField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                refreshResults();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                refreshResults();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                refreshResults();
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                prefixField.requestFocusInWindow();
            }
        });
        frame.setMinimumSize(new Dimension(720, 420));
        frame.setSize(840, 520);
        frame.setLocationByPlatform(true);
        updateWindowTitle();
        refreshResults();
        frame.setVisible(true);
    }

    private void updateWindowTitle() {
        frame.setTitle("Autocomplete — " + engineName + " — " + currentDataset.getFileName());
        datasetSelector.setToolTipText(currentDataset.toString());
    }

    private void updateDatasetSelector() {
        updatingSelector = true;
        try {
            datasetSelector.removeAllItems();
            for (Path file : availableDatasets(currentDataset)) {
                datasetSelector.addItem(file.getFileName().toString());
            }
            datasetSelector.setSelectedItem(currentDataset.getFileName().toString());
            engineSelector.setSelectedItem(engineName);
        } finally {
            updatingSelector = false;
        }
    }

    /** 同目录快速切换；只列出首行是记录数的文本文件，排除说明文件。 */
    static List<Path> availableDatasets(Path dataset) {
        Path absolute = dataset.toAbsolutePath().normalize();
        List<Path> files = new ArrayList<>();
        try (var entries = Files.list(absolute.getParent())) {
            entries.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .filter(AutocompleteGui::hasRecordCount)
                    .forEach(files::add);
        } catch (IOException exception) {
            // 即使目录不可枚举，也保留当前文件和手动打开入口。
        }
        if (!files.contains(absolute)) {
            files.add(absolute);
        }
        files.sort(Comparator.comparing(path -> path.getFileName().toString()));
        return List.copyOf(files);
    }

    private static boolean hasRecordCount(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String first = reader.readLine();
            return first != null && Integer.parseInt(first.strip()) >= 0;
        } catch (IOException | NumberFormatException exception) {
            return false;
        }
    }

    /** 先完成读取和引擎构造；全部成功后，界面才替换当前数据。 */
    static LoadedDataset loadDataset(Path dataset, String engineName) throws IOException {
        Path absolute = dataset.toAbsolutePath().normalize();
        List<Term> terms = DatasetLoader.load(absolute);
        return new LoadedDataset(absolute, EngineFactory.create(engineName, terms), terms);
    }

    private void switchDataset(Path dataset) {
        switchConfiguration(dataset, engineName);
    }

    private void switchConfiguration(Path dataset, String requestedEngine) {
        if (loading) {
            return;
        }
        loading = true;
        datasetSelector.setEnabled(false);
        engineSelector.setEnabled(false);
        openDatasetButton.setEnabled(false);
        prefixField.setEnabled(false);
        benchmarkButton.setEnabled(false);
        statusLabel.setText("正在准备 " + dataset.getFileName() + " / " + requestedEngine + "，完成后重新查询…");
        // 大文件读取与 binary 的预排序在后台进行，不计入 query 时间。
        new SwingWorker<LoadedDataset, Void>() {
            @Override
            protected LoadedDataset doInBackground() throws IOException {
                if (dataset.equals(currentDataset)) {
                    // 只切换算法时复用已解析的词条，不重复读取文件。
                    return new LoadedDataset(currentDataset,
                            EngineFactory.create(requestedEngine, currentTerms), currentTerms);
                }
                return loadDataset(dataset, requestedEngine);
            }

            @Override
            protected void done() {
                try {
                    LoadedDataset loaded = get();
                    engine = loaded.engine;
                    recordCount = loaded.recordCount;
                    currentDataset = loaded.path;
                    currentTerms = loaded.terms;
                    engineName = requestedEngine;
                    updateWindowTitle();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showLoadError(exception);
                } catch (ExecutionException exception) {
                    showLoadError(exception.getCause());
                } finally {
                    loading = false;
                    updateDatasetSelector();
                    datasetSelector.setEnabled(true);
                    engineSelector.setEnabled(true);
                    openDatasetButton.setEnabled(true);
                    prefixField.setEnabled(true);
                    benchmarkButton.setEnabled(true);
                    refreshResults();
                    prefixField.requestFocusInWindow();
                }
            }
        }.execute();
    }

    private void showLoadError(Throwable cause) {
        JOptionPane.showMessageDialog(frame,
                "未能切换数据或算法，继续使用原配置。\n" + cause.getMessage(),
                "切换失败", JOptionPane.ERROR_MESSAGE);
    }

    static final class LoadedDataset {
        final Path path;
        final AutocompleteEngine engine;
        final int recordCount;
        final List<Term> terms;

        LoadedDataset(Path path, AutocompleteEngine engine, List<Term> terms) {
            this.path = path;
            this.engine = engine;
            this.recordCount = terms.size();
            this.terms = terms;
        }
    }

    private void comparePerformance() {
        if (loading) return;
        String prefix = prefixField.getText();
        if (prefix.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "请先输入要比较的前缀，例如 Frank W。");
            return;
        }
        // 固定本次输入，避免后台测量与主窗口切换的数据混在一起。
        Path dataset = currentDataset;
        List<Term> terms = currentTerms;
        int records = recordCount;
        List<String> names = List.copyOf(EngineFactory.supportedEngineNames());
        loading = true;
        datasetSelector.setEnabled(false);
        engineSelector.setEnabled(false);
        openDatasetButton.setEnabled(false);
        prefixField.setEnabled(false);
        benchmarkButton.setEnabled(false);
        statusLabel.setText("正在比较算法：每种先预热 20 次，再测量 100 次…");
        new SwingWorker<BenchmarkRunner.Comparison, Void>() {
            @Override protected BenchmarkRunner.Comparison doInBackground() {
                return BenchmarkRunner.compare(names, name -> EngineFactory.create(name, terms),
                        prefix, limit, 20, 100);
            }
            @Override protected void done() {
                try {
                    BenchmarkRunner.Comparison result = get();
                    if (frame.isDisplayable()) BenchmarkDialog.show(frame, dataset, records, result);
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    JOptionPane.showMessageDialog(frame, "性能测试失败：" + cause.getMessage());
                } finally {
                    loading = false;
                    datasetSelector.setEnabled(true);
                    engineSelector.setEnabled(true);
                    openDatasetButton.setEnabled(true);
                    prefixField.setEnabled(true);
                    benchmarkButton.setEnabled(true);
                    refreshResults();
                }
            }
        }.execute();
    }

    private void refreshResults() {
        if (loading) {
            return;
        }
        String prefix = prefixField.getText();
        resultModel.setRowCount(0);
        if (prefix.isEmpty()) {
            statusLabel.setText(String.format(
                    "engine=%s    records=%,d    请输入查询前缀", engine.name(), recordCount));
            return;
        }

        try {
            long startedAt = System.nanoTime();
            long matchCount = engine.numberOfMatches(prefix);
            List<Term> matches = engine.allMatches(prefix, limit);
            double elapsedMilliseconds = (System.nanoTime() - startedAt) / 1_000_000.0;

            for (Term term : matches) {
                resultModel.addRow(new Object[] {term.query(), String.format("%,d", term.weight())});
            }
            statusLabel.setText(String.format(
                    "engine=%s    records=%,d    matches=%,d    query=%.3f ms",
                    engine.name(), recordCount, matchCount, elapsedMilliseconds));
        } catch (RuntimeException exception) {
            statusLabel.setText("engine=" + engine.name() + "    查询未完成：" + exception.getMessage());
        }
    }

    private static Path resolveDataset(Path requested) {
        if (requested.isAbsolute() || Files.exists(requested)) {
            return requested.normalize();
        }
        try {
            Path current = Path.of(AutocompleteGui.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).toAbsolutePath();
            while (current != null) {
                Path candidate = current.resolve(requested).normalize();
                if (Files.exists(candidate)) {
                    return candidate;
                }
                current = current.getParent();
            }
        } catch (Exception ignored) {
            // DatasetLoader 会保留原始路径并报告具体的文件读取异常。
        }
        return requested;
    }

    /** 把“稍后显示窗口”封装成明确实现 Runnable 的对象。 */
    private static final class ShowWindowTask implements Runnable {
        private final AutocompleteGui gui;
        private final Path dataset;

        private ShowWindowTask(AutocompleteGui gui, Path dataset) {
            this.gui = gui;
            this.dataset = dataset;
        }

        @Override
        public void run() {
            gui.showWindow(dataset);
        }
    }

    /** 保存启动参数的普通不可变 class。 */
    static final class LaunchOptions {
        private final Path dataset;
        private final String engineName;
        private final int limit;

        LaunchOptions(Path dataset, String engineName, int limit) {
            this.dataset = dataset;
            this.engineName = engineName;
            this.limit = limit;
        }

        Path dataset() {
            return dataset;
        }

        String engineName() {
            return engineName;
        }

        int limit() {
            return limit;
        }
    }
}
