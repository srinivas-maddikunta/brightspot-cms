package com.psddev.cms.tool;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.ObjectUtils;
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
//                if (e instanceof SettingsException) {
//                    System.out.println("[WARN] Unable to automatically verify class: " + clazz.getCanonicalName() + " should be manually verified");
//                } else if (e instanceof NoSuchElementException) {
//                    System.out.println("[WARN] Failed to analyze [" + clazz.getCanonicalName() + "]. Should be manually verified.");
//                } else {
//                    System.out.println("Unable to verify class: " + e.getMessage());
//                }
                e.printStackTrace();
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
            String baseName = ToolPageContext.Static.getResourceBaseName(context);

            ResourceBundle baseOverride = ToolPageContext.Static.findBundle(baseName + "Override", locale);
            ResourceBundle baseDefault = ToolPageContext.Static.findBundle(baseName + "Default", locale);

            ResourceBundle fallbackOverride = ToolPageContext.Static.findBundle("FallbackOverride", locale);
            ResourceBundle fallbackDefault = ToolPageContext.Static.findBundle("FallbackDefault", locale);

            if (key == null) {
                System.out.println("[WARN] Resource key from " + className + "#" + methodName + "@" + lineNumber + " is null.");
                return;
            }

            String property = ObjectUtils.firstNonNull(
                    ToolPageContext.Static.findBundleString(fallbackDefault, key),
                    ToolPageContext.Static.findBundleString(fallbackOverride, key),
                    ToolPageContext.Static.findBundleString(baseOverride, key),
                    ToolPageContext.Static.findBundleString(baseDefault, key));

            if (property == null) {
                throw new RuntimeException("Unable to find resource");
            }
        }
    }
}
