package dev.flamey.overlay;

import dev.flamey.overlay.api.server.Bedwars;
import dev.flamey.overlay.api.server.SupportedServer;
import dev.flamey.overlay.command.CommandClass;
import dev.flamey.overlay.command.CommandGUI;
import dev.flamey.overlay.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "overlay", version = "0.0.1", name = "overlay")
public class Main {

    public Minecraft mc = Minecraft.getMinecraft();

    public static ConfigManager configManager = new ConfigManager();
    public static boolean toggledGUI = false;
    public static boolean toggled = false;
    public static SupportedServer server;

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) throws InstantiationException, IllegalAccessException {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandClass());
        configManager.load();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            if (toggledGUI) {
                toggledGUI = false;
                mc.displayGuiScreen(new CommandGUI());
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e) {
        if (e.type.equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            if (toggled) Overlay.INSTANCE.draw();
        }
    }

    @SubscribeEvent
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (e.manager.getRemoteAddress().toString().toLowerCase().contains("pika")) {
            server = SupportedServer.PIKA;
        } else if (e.manager.getRemoteAddress().toString().toLowerCase().contains("jartex")) {
            server = SupportedServer.JARTEX;
        } else {
            server = SupportedServer.NONE;
        }
    }

    @SubscribeEvent
    public void serverDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        server = SupportedServer.NONE;
        Overlay.INSTANCE.reset();
    }

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent e) {
        Overlay.INSTANCE.chat(e);
    }

    @SubscribeEvent
    public void keyinput(InputEvent.KeyInputEvent e) {
        if (Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();
            if (key == CommandGUI.getKeybind()) {
                toggled = !toggled;
            }
        }
    }


}
