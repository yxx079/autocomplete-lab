package edu.course.lesson06.practice;

/** 截取展示文字。约定 text 非 null，limit 非负；不改变原始文本。 */
public final class LabelPractice {
    private LabelPractice() {}
    public static String firstPart(String text, int limit) {
        // TODO：先判断是否需要截取，再返回对应字符串。
        throw new UnsupportedOperationException("firstPart 尚未完成");
    }
    public static void main(String[] args) {
        System.out.println(firstPart("Shanghai, China", 8));
        System.out.println(firstPart("Rome", 8));
        System.out.println("[" + firstPart("", 8) + "]");
    }
}
