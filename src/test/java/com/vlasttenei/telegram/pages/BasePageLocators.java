package com.vlasttenei.telegram.pages;

public class BasePageLocators {
    // Локаторы для чата
    public static final String CHAT_LIST = "//div[contains(@class, 'chat-list')] | //div[contains(@class, 'chatlist-top has-contacts')]//ul";
    public static final String BOT_CHAT = "//span[text()='Зов Теней - Тест']/ancestor::a | //img[@alt='Зов Теней - Тест']/ancestor::a";
    public static final String PROFILE_BUTTON = "//span[text()='Профиль'] | //div[text()='Профиль']";

    // Локаторы для авторизации
    public static final String LOGIN_BY_PHONE_BUTTON = "//button[text()='Log in by phone Number' or span[text()='Log in by phone Number']]";
    public static final String PHONE_INPUT = "//input[@id='sign-in-phone-number'] | //label[span[text()='Phone Number']]/preceding-sibling::div[contains(@class, 'input-field-input')]";
    public static final String NEXT_BUTTON = "//button[contains(@class,'Button smaller primary')] | //button[.//span[text()='Next']]";
    public static final String CODE_INPUT = "//div[contains(@class, 'input-field')]//input | //input[@id='sign-in-code']";
} 