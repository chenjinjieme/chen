package com.chen.util.data;

import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public abstract class ControllerUtil {
    public static Map<String, Object> putData(Page data) {
        return data.size() != 0 ? new Data(data) : noData();
    }

    public static Map<String, Object> putData(List data) {
        return data.size() != 0 && data.get(0) != null ? new Data(data) : noData();
    }

    public static Map<String, Object> putData(Object data) {
        return data != null ? new Data(data) : noData();
    }

    public static Map<String, Object> execute(int i) {
        if (i == 0) throw new RuntimeException();
        return success();
    }

    public static Map<String, Object> execute(int i, Action action) {
        if (i == 0) {
            action.action();
            throw new RuntimeException();
        }
        return success();
    }

    public static Map<String, Object> success() {
        return Success.INSTANCE.message;
    }

    public static Map<String, Object> noData() {
        return NoData.INSTANCE.message;
    }

    public static Map<String, Object> incorrect() {
        return Incorrect.INSTANCE.message;
    }

    public static Map<String, Object> invalid() {
        return Invalid.INSTANCE.message;
    }

    public static Map<String, Object> duplicate() {
        return Duplicate.INSTANCE.message;
    }

    public static Map<String, Object> noRight() {
        return NoRight.INSTANCE.message;
    }

    public static Map<String, Object> fail() {
        return Fail.INSTANCE.message;
    }

    private enum Success {
        INSTANCE;
        private final Message message = new Message("success");
    }

    private enum NoData {
        INSTANCE;
        private final Message message = new Message("noData");
    }

    private enum Incorrect {
        INSTANCE;
        private final Message message = new Message("incorrect");
    }

    private enum Invalid {
        INSTANCE;
        private final Message message = new Message("invalid");
    }

    private enum Duplicate {
        INSTANCE;
        private final Message message = new Message("duplicate");
    }

    private enum NoRight {
        INSTANCE;
        private final Message message = new Message("noRight");
    }

    private enum Fail {
        INSTANCE;
        private final Message message = new Message("fail");
    }

    @FunctionalInterface
    public interface Action {
        void action();
    }
}
