package com.psddev.cms.tool;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.SettingsException;
import com.psddev.dari.util.sa.Jvm;
import com.psddev.dari.util.sa.JvmMethodListener;
import com.psddev.dari.util.sa.JvmObject;

public class LocalizationVerifier {

    public static void main(String[] paths) throws IOException {
        Jvm jvm = new Jvm();

        try {
            LocalizeMethodListener listener = new LocalizeMethodListener();
            jvm.addMethodListener(ToolPageContext.class.getMethod("localize", Object.class, String.class), listener);
            jvm.addMethodListener(ToolPageContext.class.getMethod("localize", Object.class, Map.class, String.class), listener);
        } catch (Exception e) {
            // ignore
        }

        Set<Class<?>> classes = ClassFinder.findClasses(Object.class);
        classes.removeIf(c -> c.getCanonicalName() == null || !c.getCanonicalName().startsWith("com.psddev.cms.tool"));
        classes.removeIf(c -> c.equals(LocalizationVerifier.class));

        System.out.println("--- Begin Localization Resource Validation ---");

        for (Class clazz : classes) {

            try {
                jvm.analyze(clazz);
            } catch (Exception e) {
                if (e instanceof SettingsException) {
                    System.out.println("[WARNING] Requires database to automatically verify localization keys in " + clazz.getCanonicalName() + ", should be manually verified");
                } else if (e instanceof NoSuchElementException) {
                    System.out.println("[WARNING] Failed to analyze [" + clazz.getCanonicalName() + "]. Should be manually verified.");
                } else {
                    throw new RuntimeException("[ERROR] Failed to verify ToolPageContext#localize usages in " + clazz.getCanonicalName());
                }
            }
        }

        System.out.println("--- End Localization Resource Validation ---");
    }

    private static class LocalizeMethodListener extends JvmMethodListener {

        @Override
        public void onInvocation(
                Method callingMethod,
                int callingLine,
                Method calledMethod,
                int calledLine,
                JvmObject calledObject,
                List<JvmObject> calledArguments,
                JvmObject returnedObject) {

            new Usage(
                    callingMethod.getDeclaringClass().getName(),
                    callingMethod.getName(),
                    calledLine,
                    calledArguments.get(0).resolve(),
                    calledArguments.size() > 2 ? (Map<String, Object>) calledArguments.get(1).resolve() : null,
                    (String) calledArguments.get(calledArguments.size() - 1).resolve()).verify();

        }
    }

    private static class Usage {

        private final String className;
        private final String methodName;
        private final int lineNumber;

        private final Object context;
        private final Map<String, Object> contextOverrides;
        private final String key;

        protected Usage(String className, String methodName, int lineNumber, Object context, Map<String, Object> contextOverrides, String key) {
            this.className = className;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
            this.context = context;
            this.contextOverrides = contextOverrides;
            this.key = key;
        }

        private void verify() {

            if (context instanceof Recordable) {
                System.out.println("[WARN] Localization context from " + className + "#" + methodName + "@" + lineNumber + " is from Recordable object, will require manual validation.");
                return;
            }

            Locale locale = Locale.US;
            String baseName = ToolPageContext.getResourceBaseName(context);

            ResourceBundle baseOverride = ToolPageContext.findBundle(baseName + "Override", locale);
            ResourceBundle baseDefault = ToolPageContext.findBundle(baseName + "Default", locale);

            ResourceBundle fallbackOverride = ToolPageContext.findBundle("FallbackOverride", locale);
            ResourceBundle fallbackDefault = ToolPageContext.findBundle("FallbackDefault", locale);

            if (key == null) {
                System.out.println("[WARNING] Resource key from " + className + "#" + methodName + "@" + lineNumber + " is null.");
                return;
            }

            String property = ObjectUtils.firstNonNull(
                    ToolPageContext.findBundleString(fallbackDefault, key),
                    fallbackDefault != null ? ToolPageContext.findBundleString(fallbackOverride, key) : null,
                    baseOverride != null ? ToolPageContext.findBundleString(baseOverride, key) : null,
                    baseDefault != null ? ToolPageContext.findBundleString(baseDefault, key) : null);

            if (property == null) {
                throw new RuntimeException("[ERROR] Unable to find resource key [" + key + "]");
            }
        }
    }
}
