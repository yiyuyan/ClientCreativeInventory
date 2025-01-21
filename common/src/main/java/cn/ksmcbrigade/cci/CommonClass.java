package cn.ksmcbrigade.cci;

import cn.ksmcbrigade.cci.platform.Services;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.

    public static final KeyMapping key = new KeyMapping("key.cci.name", InputConstants.KEY_Y,KeyMapping.CATEGORY_CREATIVE);

    public static boolean big = true;

    public static void init() {

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        try {
            File file = new File("config/cci-config.json");
            if(!file.exists()){
                JsonObject object = new JsonObject();
                object.addProperty("bigInventory",true);
                FileUtils.writeStringToFile(file,object.toString());
            }
            JsonObject object = JsonParser.parseString(FileUtils.readFileToString(file)).getAsJsonObject();
            big = object.get("bigInventory").getAsBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Services.PLATFORM.isModLoaded("cci")) {
            Constants.LOG.info("Hello to cci");
        }
    }
}
