package com.MobileFolk.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

import static com.MobileFolk.utils.WebUI.getLanguageValue;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ModelMapper {
    public String devName;         // DEV Name
    public String description;     // Mô tả trên website
    public String value;           // Value
    public String title;
    public String msField;
    public boolean fill;           // Column không điền
    public boolean verify;         // Column cần verify
    public String langProperty;     // language property
    public boolean isMultiValue;


    public ModelMapper(ModelMapper model) {
        this.devName = model.getDevName();
        this.description = model.getDescription();
        this.value = model.getValue();
        this.title = model.getTitle();
        this.msField = model.getMsField();
        this.fill = model.isFill();
        this.verify = model.isVerify();
        this.langProperty = model.getLangProperty();
        this.isMultiValue = model.isMultiValue();
    }

    public String getTitle() {
        if (Objects.nonNull(this.langProperty) && !Strings.isEmpty(this.langProperty)) {
            this.title = getLanguageValue(this.langProperty);
            return this.title;
        }

        if (Objects.nonNull(this.title) && !Strings.isEmpty(this.title)) {
            return this.title;
        }
        return "";
    }

    /**
     * Update the title when re-change langProperty
     *
     * @param langProperty : property in bundle
     */
    public void updateLangProperty(String langProperty) {
        this.langProperty = langProperty;
        getTitle();
    }

    public void updateMultiValue(String... values) {
        String val;
        if (values.length == 0) val = value;
        else {
            val = values[0];
        }

        if (val.contains(":")) {
            this.isMultiValue = true;
        }
    }
}
