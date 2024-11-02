package me.aemo.addons.utils;

import me.aemo.addons.enums.Languages;

import java.util.HashMap;
import java.util.Map;

public class Translator {
    private Languages currentLanguage = Languages.English;
    private final Map<String, String[]> translations = new HashMap<>();

    public Translator() {
        loadTranslations();
    }

    private void loadTranslations() {
        translations.put("product", new String[]{"Product", "الصنف"});
        translations.put("quantity", new String[]{"Quantity", "العدد"});
        translations.put("unit", new String[]{"Unit", "الوحدة"});
        translations.put("length", new String[]{"Length", "الطول"});
        translations.put("height", new String[]{"Height", "الارتفاع"});
        translations.put("surface", new String[]{"Surface", "المسطح"});
        translations.put("price", new String[]{"Price", "سعر المتر"});
        translations.put("save", new String[]{"Save", "حفظ"});
        translations.put("cancel", new String[]{"Cancel", "إالغاء"});
        translations.put("add", new String[]{"Add", "إضافة"});
        translations.put("display_all", new String[]{"Display All", "عرض الكل"});
        translations.put("cm_unit", new String[]{"cm", "سم"});
        translations.put("m_unit", new String[]{"m", "متر"});
        translations.put("product_added", new String[]{"Product added successfully!", "تمت إضافه الصنف"});
        translations.put("valid_num", new String[]{"Please enter valid numerical values.","الرجاء ادخال رقم..."});
        translations.put("all_product", new String[]{"All Product", "كل الأصناف"});
        translations.put("edit", new String[]{"Edit", "تعديل"});
        translations.put("remove", new String[]{"Remove", "حذف"});
        translations.put("remove_all", new String[]{"Remove All", "حذف الكل"});
        translations.put("ok", new String[]{"OK", "حسنا"});
        translations.put("select_product", new String[]{"Please select a product to edit.", "الرجاء إختيار الصنف اولا.."});
        translations.put("edit_product", new String[]{"Edit Product", "تعديل الصنف"});

        translations.put("open_file", new String[]{"Open File", "فتح الملف"});
        translations.put("open_location", new String[]{"Open Location", "فتح موقع الملف"});
        translations.put("close", new String[]{"Close", "إغلاق"});
        translations.put("saved_to", new String[]{"Data saved to ", "تم حفظ الملف في "});
        translations.put("file_saved", new String[]{"File Saved", "تم حفظ الملف"});
    }


    public void setLanguage(Languages language) {
        this.currentLanguage = language;
    }
    public Languages getLanguage(){
        return currentLanguage;
    }

    public String translate(String key) {
        String[] translation = translations.get(key);
        if (translation != null) {
            return currentLanguage == Languages.Arabic ? translation[1] : translation[0];
        }
        System.err.println("Translation key not found: " + key);
        return key;
    }

}
