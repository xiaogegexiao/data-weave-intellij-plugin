package org.mule.tooling.bat.launcher;


import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveBasedConfiguration;

import java.util.Arrays;
import java.util.Collection;

public class BatTestConfiguration extends ModuleBasedConfiguration implements ModuleRunProfile, RunConfigurationWithSuppressedDefaultDebugAction, WeaveBasedConfiguration {

  public static final String PREFIX = "Bat-";
  public static final String WEAVE_FILE = PREFIX + "NameIdentifier";
  public static final String VMOPTIONS = PREFIX + "VMOptions";

  private Project project;
  private String nameIdentifier;
  private String vmOptions;

  protected BatTestConfiguration(String name, @NotNull ConfigurationFactory factory, Project project) {
    super(name, new JavaRunConfigurationModule(project, true), factory);
    this.project = project;
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new BatTestRunnerEditor(this);
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
    return new BatTestRunnerCommandLine(executionEnvironment, this);
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    this.nameIdentifier = JDOMExternalizerUtil.readField(element, WEAVE_FILE);
    this.vmOptions = JDOMExternalizerUtil.readField(element, VMOPTIONS);
    getConfigurationModule().readExternal(element);
  }


  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    // Stores the values of this class into the parent
    JDOMExternalizerUtil.writeField(element, WEAVE_FILE, this.getNameIdentifier());
    JDOMExternalizerUtil.writeField(element, VMOPTIONS, this.getVmOptions());
    getConfigurationModule().writeExternal(element);
  }

  @Override
  public Collection<Module> getValidModules() {
    final ModuleManager moduleManager = ModuleManager.getInstance(this.project);
    return Arrays.asList(moduleManager.getModules());
  }


  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    if (StringUtils.isBlank(getNameIdentifier())) {
      throw new RuntimeConfigurationException("Name Identifier can not be empty.");
    }
    if (getModule() == null) {
      throw new RuntimeConfigurationException("Module can not be empty.");
    }
    super.checkConfiguration();
  }

  public String getVmOptions() {
    return vmOptions;
  }

  public String getNameIdentifier() {
    return nameIdentifier;
  }

  public void setVmOptions(String vmOptions) {
    this.vmOptions = vmOptions;
  }

  public void setNameIdentifier(String nameIdentifier) {
    this.nameIdentifier = nameIdentifier;
  }

  public Module getModule() {
    return getConfigurationModule().getModule();
  }

}
