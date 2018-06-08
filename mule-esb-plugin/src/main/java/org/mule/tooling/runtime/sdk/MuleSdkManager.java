package org.mule.tooling.runtime.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MuleSdkManager implements ApplicationComponent {

    public static MuleSdkManager getInstance() {
        return ApplicationManager.getApplication().getComponent(MuleSdkManager.class);
    }

    private Map<String, MuleSdk> runtimes = new HashMap<>();

    @Override
    public void initComponent() {
        File muleDistroDirectory = getMuleDistroDirectory();
        File[] distros = muleDistroDirectory.listFiles();
        for (File distro : distros) {
            MuleSdk sdk = new MuleSdk(distro.getAbsolutePath());
            runtimes.put(sdk.getVersion(), sdk);
        }
    }

    public MuleSdk getSdkByVersion(String muleVersion) {
        MuleSdk muleSdk = runtimes.get(muleVersion);
        if (muleSdk == null) {
            MuleSdk sdk = MuleSdkManagerStore.getInstance().findSdk(muleVersion);
            if (sdk == null) {
                //TODO
                return null;
            } else {
                return sdk;
            }
        } else {
            return muleSdk;
        }
    }

    public File getMuleDistroDirectory() {
        return new File(System.getProperty("user.home"), ".mule_runtimes");
    }
}
