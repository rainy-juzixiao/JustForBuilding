/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PreviewerProcessor extends AbstractProcessor {

    private static final String ANNOTATION = "net.rainy_juzixiao.justforbuilding.preview.Previewer";
    private static final String REGISTRY = "net.rainy_juzixiao.justforbuilding.preview.PreviewerRegistry";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ANNOTATION);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        TypeElement annotation = processingEnv.getElementUtils().getTypeElement(ANNOTATION);
        if (annotation == null) {
            return false;
        }
        List<String> calls = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            if (element instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) element;
                TypeElement owner = (TypeElement) method.getEnclosingElement();
                calls.add(owner.getQualifiedName() + "." + method.getSimpleName() + "();");
            }
        }
        if (calls.isEmpty()) {
            return false;
        }
        StringBuilder source = new StringBuilder();
        source.append("package net.rainy_juzixiao.justforbuilding.preview;\n\n");
        source.append("public final class PreviewerRegistry {\n");
        source.append("    private PreviewerRegistry() {\n    }\n\n");
        source.append("    public static void registerAll() {\n");
        for (String call : calls) {
            source.append("        ").append(call).append("\n");
        }
        source.append("    }\n}\n");
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(REGISTRY);
            try (Writer writer = file.openWriter()) {
                writer.write(source.toString());
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to generate " + REGISTRY + ": " + e.getMessage());
        }
        return true;
    }
}
