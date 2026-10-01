package edu.course.autocomplete.app;

import edu.course.autocomplete.benchmark.BenchmarkRunner;
import java.awt.BorderLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** 展示一次性能测试的固定快照，切换主窗口数据不会改变这份报告。 */
final class BenchmarkDialog {
    private BenchmarkDialog() { }

    static void show(JFrame owner, Path dataset, int records, BenchmarkRunner.Comparison report) {
        JDialog dialog = new JDialog(owner, "性能对比结果", false);
        DefaultTableModel model = new DefaultTableModel(
                new Object[] {"算法", "匹配数", "通常耗时（ms）", "95% 查询不超过（ms）", "状态"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (BenchmarkRunner.Measurement row : report.measurements()) {
            model.addRow(new Object[] {row.engine(), row.matches() == null ? "—" : row.matches(),
                    milliseconds(row.medianNanos()), milliseconds(row.p95Nanos()),
                    row.succeeded() ? "完成" : "失败：" + row.error()});
        }
        String agreement = report.resultsConsistent() == null ? "未能完成全部算法，暂不能判定一致性"
                : report.resultsConsistent() ? "匹配数、展示顺序、文本和权重一致"
                : "结果不一致，请先检查算法实现";
        JTextArea details = new JTextArea("数据：" + dataset + "\n记录数：" + records
                + "    前缀：" + report.prefix() + "    最多展示 " + report.limit() + " 条匹配结果"
                + "\n预热：" + report.warmupRounds() + " 次    测量：" + report.measureRounds()
                + " 次\n" + agreement
                + "\n计时包含计数与取结果，不包含数据载入、引擎构建或界面绘制。"
                + "\n通常耗时：将测量结果从快到慢排列，取中间位置的耗时（中位数）。\n95% 查询不超过：约 95% 的测量耗时在这个值以内（P95），它不是最大耗时。\n耗时越小越快；具体数值随机器和运行状态变化。");
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(details, BorderLayout.NORTH);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(4).setPreferredWidth(260);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton export = new JButton("导出 CSV…");
        JLabel saveStatus = new JLabel("导出会保存这次测量结果，不会重新测试。");
        export.addActionListener(event -> {
            Path results = defaultResultsDirectory();
            try {
                Files.createDirectories(results);
            } catch (java.io.IOException exception) {
                saveStatus.setText("无法创建结果文件夹：" + exception.getMessage());
                return;
            }
            JFileChooser chooser = new JFileChooser(results.toFile());
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));
            chooser.setSelectedFile(results.resolve("benchmark-" + timestamp + ".csv").toFile());
            if (chooser.showSaveDialog(dialog) != JFileChooser.APPROVE_OPTION) return;
            Path selected = chooser.getSelectedFile().toPath();
            if (!selected.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {
                selected = selected.resolveSibling(selected.getFileName() + ".csv");
            }
            final Path output = selected;
            if (Files.exists(output) && JOptionPane.showConfirmDialog(dialog, "覆盖已有文件？\n" + output,
                    "确认保存", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            export.setEnabled(false);
            saveStatus.setText("正在保存…");
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    BenchmarkRunner.export(report, dataset, records, output);
                    return null;
                }
                @Override protected void done() {
                    try { get(); saveStatus.setText("已保存：" + output); }
                    catch (Exception exception) {
                        Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                        saveStatus.setText("保存失败：" + cause.getMessage());
                    } finally { export.setEnabled(true); }
                }
            }.execute();
        });
        JPanel footer = new JPanel(new BorderLayout(8, 0));
        footer.add(saveStatus, BorderLayout.CENTER);
        footer.add(export, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setSize(1060, 470);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    // 从编译输出向上查找本工程，避免选择外部数据时把报告写到数据目录。
    private static Path defaultResultsDirectory() {
        try {
            Path compiled = Path.of(BenchmarkDialog.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path project = findProjectRoot(compiled);
            if (project != null) return project.resolve("results");
        } catch (Exception ignored) {
            // 特殊启动环境下，回退到工作目录中的工程或工作目录本身。
        }
        Path working = Path.of("").toAbsolutePath().normalize();
        Path project = findProjectRoot(working);
        return (project == null ? working : project).resolve("results");
    }

    static Path findProjectRoot(Path start) {
        for (Path current = start.toAbsolutePath().normalize(); current != null; current = current.getParent()) {
            if (Files.isRegularFile(current.resolve("pom.xml"))) return current;
        }
        return null;
    }

    private static String milliseconds(Long nanos) {
        return nanos == null ? "—" : String.format(Locale.ROOT, "%.3f", nanos / 1_000_000.0);
    }
}
