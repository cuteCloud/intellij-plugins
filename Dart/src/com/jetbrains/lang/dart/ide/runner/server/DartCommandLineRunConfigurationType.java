// Copyright 2000-2018 JetBrains s.r.o.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.jetbrains.lang.dart.ide.runner.server;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.lang.dart.DartBundle;
import com.jetbrains.lang.dart.DartFileType;
import icons.DartIcons;
import org.jetbrains.annotations.NotNull;

public class DartCommandLineRunConfigurationType extends ConfigurationTypeBase implements DumbAware {
  public static DartCommandLineRunConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(DartCommandLineRunConfigurationType.class);
  }

  public DartCommandLineRunConfigurationType() {
    super("DartCommandLineRunConfigurationType",
          DartBundle.message("runner.command.line.configuration.name"),
          DartBundle.message("runner.command.line.configuration.description"),
          lazyIcon(() -> DartIcons.Dart_16));
    addFactory(new ConfigurationFactory(this) {
      @Override
      public String getName() {
        return "Dart Command Line Application"; // compatibility
      }

      @NotNull
      @Override
      public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new DartCommandLineRunConfiguration("Dart", project, DartCommandLineRunConfigurationType.this);
      }

      @Override
      public boolean isApplicable(@NotNull Project project) {
        return FileTypeIndex.containsFileOfType(DartFileType.INSTANCE, GlobalSearchScope.projectScope(project));
      }
    });
  }
}
