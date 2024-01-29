package com.MobileFolk.Common;

public class BaseConst {

    public static final String DYNAMIC_A_TEXT = "//a[text()='%s']";
    public static final String DYNAMIC_INPUT_NAME_FORM = "//input[@name='%s']";
    public static final String DYNAMIC_BUTTON_TEXT_FORM = "//button[text()='%s']";
    public static final String DYNAMIC_HEADING_TEXT = "//%s[text()='%s']";
    public static final String DYNAMIC_OPTION_TEXT_FORM = "//label[text()='%s']";
    public static final String DYNAMIC_LABEL_TEXT_FORM = "//label[text()='%s']";
    public static final String DYNAMIC_P_TEXT = "//p[text()='%s']";
    public static final String DYNAMIC_LI_TEXT = "//li[text()='%s']";
    public static final String DYNAMIC_LABEL_SPAN_TEXT = "//label[text()='%s']//following::span[text()='%s']";
    public static final String DYNAMIC_DIV_TEXT="//div[text()='%s']";


    public enum FilterOption {
        EQUAL("opt_equal"), NOT_EQUAL("opt_not_equal"), CONTAIN("opt_contain"), NOT_CONTAIN("opt_not_contain"), EMPTY("opt_empty"), FILLED("opt_filled"), SMALLER("opt_smaller"), SMALLER_EQUAL("opt_smaller_equal"), GREATER("opt_greater"), GREATER_EQUAL("opt_greater_equal"), START_WITH("opt_start_with"), END_WITH("opt_end_with");

        private String name;

        private FilterOption(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

