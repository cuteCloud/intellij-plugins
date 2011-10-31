package com.intellij.lang.javascript.flex.run;

import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.lang.javascript.flex.FlexBundle;
import com.intellij.lang.javascript.flex.actions.airmobile.MobileAirUtil;
import com.intellij.lang.javascript.flex.projectStructure.model.*;
import com.intellij.lang.javascript.flex.projectStructure.model.impl.Factory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import static com.intellij.lang.javascript.flex.run.AirMobileRunnerParameters.*;

public class FlexIdeRunnerParameters extends BCBasedRunnerParameters implements Cloneable {

  private boolean myOverrideMainClass = false;
  private String myOverriddenMainClass = "";
  private String myOverriddenOutputFileName = "";

  private boolean myLaunchUrl = false;
  private @NotNull String myUrl = "http://";

  private @NotNull LauncherParameters myLauncherParameters = new LauncherParameters();
  private boolean myRunTrusted = true;

  private @NotNull String myAdlOptions = "";
  private @NotNull String myAirProgramParameters = "";

  private @NotNull AirMobileRunTarget myMobileRunTarget = AirMobileRunTarget.Emulator;
  private @NotNull Emulator myEmulator = Emulator.NexusOne;
  private int myScreenWidth = 0;
  private int myScreenHeight = 0;
  private int myFullScreenWidth = 0;
  private int myFullScreenHeight = 0;
  private @NotNull AirMobileDebugTransport myDebugTransport = AirMobileDebugTransport.USB;
  private int myUsbDebugPort = MobileAirUtil.DEBUG_PORT_DEFAULT;
  private @NotNull String myEmulatorAdlOptions = "";

  public boolean isOverrideMainClass() {
    return myOverrideMainClass;
  }

  public void setOverrideMainClass(final boolean overrideMainClass) {
    myOverrideMainClass = overrideMainClass;
  }

  public String getOverriddenMainClass() {
    return myOverriddenMainClass;
  }

  public void setOverriddenMainClass(final String overriddenMainClass) {
    myOverriddenMainClass = overriddenMainClass;
  }

  public String getOverriddenOutputFileName() {
    return myOverriddenOutputFileName;
  }

  public void setOverriddenOutputFileName(final String overriddenOutputFileName) {
    myOverriddenOutputFileName = overriddenOutputFileName;
  }

  public boolean isLaunchUrl() {
    return myLaunchUrl;
  }

  public void setLaunchUrl(final boolean launchUrl) {
    myLaunchUrl = launchUrl;
  }

  @NotNull
  public String getUrl() {
    return myUrl;
  }

  public void setUrl(@NotNull final String url) {
    myUrl = url;
  }

  @NotNull
  public LauncherParameters getLauncherParameters() {
    return myLauncherParameters;
  }

  public void setLauncherParameters(@NotNull final LauncherParameters launcherParameters) {
    myLauncherParameters = launcherParameters;
  }

  public boolean isRunTrusted() {
    return myRunTrusted;
  }

  public void setRunTrusted(final boolean runTrusted) {
    myRunTrusted = runTrusted;
  }

  @NotNull
  public String getAdlOptions() {
    return myAdlOptions;
  }

  public void setAdlOptions(@NotNull final String adlOptions) {
    myAdlOptions = adlOptions;
  }

  @NotNull
  public String getAirProgramParameters() {
    return myAirProgramParameters;
  }

  public void setAirProgramParameters(@NotNull final String airProgramParameters) {
    myAirProgramParameters = airProgramParameters;
  }

  @NotNull
  public AirMobileRunTarget getMobileRunTarget() {
    return myMobileRunTarget;
  }

  public void setMobileRunTarget(@NotNull final AirMobileRunTarget mobileRunTarget) {
    myMobileRunTarget = mobileRunTarget;
  }

  @NotNull
  public Emulator getEmulator() {
    return myEmulator;
  }

  public void setEmulator(@NotNull final Emulator emulator) {
    myEmulator = emulator;
  }

  public int getScreenWidth() {
    return myScreenWidth;
  }

  public void setScreenWidth(final int screenWidth) {
    myScreenWidth = screenWidth;
  }

  public int getScreenHeight() {
    return myScreenHeight;
  }

  public void setScreenHeight(final int screenHeight) {
    myScreenHeight = screenHeight;
  }

  public int getFullScreenWidth() {
    return myFullScreenWidth;
  }

  public void setFullScreenWidth(final int fullScreenWidth) {
    myFullScreenWidth = fullScreenWidth;
  }

  public int getFullScreenHeight() {
    return myFullScreenHeight;
  }

  public void setFullScreenHeight(final int fullScreenHeight) {
    myFullScreenHeight = fullScreenHeight;
  }

  @NotNull
  public AirMobileDebugTransport getDebugTransport() {
    return myDebugTransport;
  }

  public void setDebugTransport(@NotNull final AirMobileDebugTransport debugTransport) {
    myDebugTransport = debugTransport;
  }

  public int getUsbDebugPort() {
    return myUsbDebugPort;
  }

  public void setUsbDebugPort(final int usbDebugPort) {
    myUsbDebugPort = usbDebugPort;
  }

  @NotNull
  public String getEmulatorAdlOptions() {
    return myEmulatorAdlOptions;
  }

  public void setEmulatorAdlOptions(@NotNull final String emulatorAdlOptions) {
    myEmulatorAdlOptions = emulatorAdlOptions;
  }

  public void check(final Project project) throws RuntimeConfigurationError {
    doCheck(super.checkAndGetModuleAndBC(project));
  }

  public Pair<Module, FlexIdeBuildConfiguration> checkAndGetModuleAndBC(final Project project) throws RuntimeConfigurationError {
    final Pair<Module, FlexIdeBuildConfiguration> moduleAndBC = super.checkAndGetModuleAndBC(project);
    doCheck(moduleAndBC);

    if (myOverrideMainClass) {
      final ModifiableFlexIdeBuildConfiguration overriddenBC = Factory.getCopy(moduleAndBC.second);
      overriddenBC.setMainClass(myOverriddenMainClass);
      overriddenBC.setOutputFileName(myOverriddenOutputFileName);
      overriddenBC.getAndroidPackagingOptions().setPackageFileName(FileUtil.getNameWithoutExtension(myOverriddenOutputFileName) + ".apk");
      overriddenBC.getIosPackagingOptions().setPackageFileName(FileUtil.getNameWithoutExtension(myOverriddenOutputFileName) + ".ipa");

      if (overriddenBC.getOutputType() != OutputType.Application) {
        overriddenBC.setOutputType(OutputType.Application);
        overriddenBC.setUseHtmlWrapper(false);

        overriddenBC.getDependencies().setFrameworkLinkage(LinkageType.Merged);

        for (ModifiableDependencyEntry entry : overriddenBC.getDependencies().getModifiableEntries()) {
          if (entry.getDependencyType().getLinkageType() == LinkageType.External) {
            entry.getDependencyType().setLinkageType(LinkageType.Merged);
          }
        }

        overriddenBC.getAirDesktopPackagingOptions().setUseGeneratedDescriptor(true);

        final ModifiableAndroidPackagingOptions androidOptions = overriddenBC.getAndroidPackagingOptions();
        androidOptions.setEnabled(true);
        androidOptions.setUseGeneratedDescriptor(true);
        androidOptions.getSigningOptions().setUseTempCertificate(true);

        overriddenBC.getIosPackagingOptions().setEnabled(false); // impossible without extra user input: app id, provisioning, etc.
      }

      return Pair.create(moduleAndBC.first, ((FlexIdeBuildConfiguration)overriddenBC));
    }

    return moduleAndBC;
  }

  private void doCheck(final Pair<Module, FlexIdeBuildConfiguration> moduleAndBC) throws RuntimeConfigurationError {
    final FlexIdeBuildConfiguration bc = moduleAndBC.second;

    if (myOverrideMainClass) {
      if (myOverriddenMainClass.isEmpty()) {
        throw new RuntimeConfigurationError(FlexBundle.message("main.class.not.set"));
      }
      // todo check main class presence when it becomes reliable
      if (myOverriddenOutputFileName.isEmpty()) {
        throw new RuntimeConfigurationError(FlexBundle.message("output.file.name.not.specified"));
      }
      if (!myOverriddenOutputFileName.toLowerCase().endsWith(".swf")) {
        throw new RuntimeConfigurationError(FlexBundle.message("output.file.must.have.swf.extension"));
      }
    }
    else {
      if (bc.getOutputType() != OutputType.Application) {
        throw new RuntimeConfigurationError(FlexBundle.message("bc.does.not.produce.app", getBCName(), getModuleName()));
      }
    }

    if (bc.getTargetPlatform() == TargetPlatform.Web) {
      if (myLaunchUrl) {
        try {
          new URL(myUrl);
        }
        catch (MalformedURLException e) {
          throw new RuntimeConfigurationError(FlexBundle.message("flex.run.config.incorrect.url"));
        }

        if (myLauncherParameters.getLauncherType() == LauncherParameters.LauncherType.Player) {
          throw new RuntimeConfigurationError(FlexBundle.message("flex.run.config.url.can.not.be.run.with.flash.player"));
        }
      }

      if (myLauncherParameters.getLauncherType() == LauncherParameters.LauncherType.Player
          && bc.getTargetPlatform() == TargetPlatform.Web && bc.isUseHtmlWrapper()) {
        throw new RuntimeConfigurationError(FlexBundle.message("html.wrapper.can.not.be.run.with.flash.player"));
      }
    }

    if (bc.getTargetPlatform() == TargetPlatform.Mobile) {
      if (bc.getOutputType() == OutputType.Application &&
          myMobileRunTarget == AirMobileRunTarget.AndroidDevice &&
          !bc.getAndroidPackagingOptions().isEnabled()) {
        throw new RuntimeConfigurationError(
          FlexBundle.message("android.disabled.in.bc", getBCName(), getModuleName()));
      }
    }
  }

  protected FlexIdeRunnerParameters clone() {
    final FlexIdeRunnerParameters clone = (FlexIdeRunnerParameters)super.clone();
    clone.myLauncherParameters = myLauncherParameters.clone();
    return clone;
  }

  public boolean equals(final Object o) {
    if (!super.equals(o)) return false;

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final FlexIdeRunnerParameters that = (FlexIdeRunnerParameters)o;

    if (myFullScreenHeight != that.myFullScreenHeight) return false;
    if (myFullScreenWidth != that.myFullScreenWidth) return false;
    if (myLaunchUrl != that.myLaunchUrl) return false;
    if (myRunTrusted != that.myRunTrusted) return false;
    if (myScreenHeight != that.myScreenHeight) return false;
    if (myScreenWidth != that.myScreenWidth) return false;
    if (myUsbDebugPort != that.myUsbDebugPort) return false;
    if (!myAdlOptions.equals(that.myAdlOptions)) return false;
    if (!myAirProgramParameters.equals(that.myAirProgramParameters)) return false;
    if (myDebugTransport != that.myDebugTransport) return false;
    if (myEmulator != that.myEmulator) return false;
    if (!myEmulatorAdlOptions.equals(that.myEmulatorAdlOptions)) return false;
    if (!myLauncherParameters.equals(that.myLauncherParameters)) return false;
    if (myMobileRunTarget != that.myMobileRunTarget) return false;
    if (!myUrl.equals(that.myUrl)) return false;

    return true;
  }
}
