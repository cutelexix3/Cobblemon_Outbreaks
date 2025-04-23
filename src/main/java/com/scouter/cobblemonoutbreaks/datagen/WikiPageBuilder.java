package com.scouter.cobblemonoutbreaks.datagen;

import java.util.ArrayList;
import java.util.List;

public class WikiPageBuilder {
    private StringBuilder content;
    private String title;
    private List<String> headings;

    public WikiPageBuilder(String title) {
        this.title = title;
        this.content = new StringBuilder("# ").append(title).append("\n\n");
        this.headings = new ArrayList<>();
    }

    public WikiPageBuilder addParagraph(String text) {
        this.content.append(text).append("\n\n");
        return this;
    }

    public WikiPageBuilder addFormattedParagraph(String text, Object... args) {
        this.content.append(String.format(text, args)).append("\n\n");
        return this;
    }

    public WikiPageBuilder addHeading(String text, int level) {
        String heading = "#".repeat(Math.max(0, level)) + " " + text;
        this.content.append(heading).append("\n\n");
        this.headings.add(heading);
        return this;
    }

    public WikiPageBuilder addList(String[] items) {
        for (String item : items) {
            this.content.append("- ").append(item).append("\n");
        }
        this.content.append("\n");
        return this;
    }

    public WikiPageBuilder addCodeBlock(String code) {
        this.content.append("```\n").append(code).append("\n```\n\n");
        return this;
    }

    public WikiPageBuilder addImage(String altText, String imageUrl) {
        this.content.append("![")
                .append(altText)
                .append("](")
                .append(imageUrl)
                .append(")")
                .append("\n\n");
        return this;
    }


    public WikiPageBuilder startCollapsibleSection(String sectionTitle) {
        this.content.append("<details>\n");
        this.content.append("<summary>").append(sectionTitle).append("</summary>\n\n");
        return this;
    }

    public WikiPageBuilder endCollapsibleSection() {
        this.content.append("</details>\n\n");
        return this;
    }


    public WikiPageBuilder addCollapsibleSection(String sectionTitle, String sectionContent) {
        this.content.append("<details>\n");
        this.content.append("<summary>").append(sectionTitle).append("</summary>\n\n");
        this.content.append(sectionContent).append("\n");
        this.content.append("</details>\n\n");
        return this;
    }

    public WikiPageBuilder addLink(String text, String url) {
        this.content.append("[").append(text).append("](").append(url).append(")").append("\n\n");
        return this;
    }

    public String getContent() {
        return this.content.toString();
    }

    public List<String> getHeadings() {
        return headings;
    }

    public String getTitle() {
        return title;
    }
}