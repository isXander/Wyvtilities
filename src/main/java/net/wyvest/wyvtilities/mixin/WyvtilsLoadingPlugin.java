package net.wyvest.wyvtilities.mixin;

import kotlin.KotlinVersion;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import xyz.matthewtgm.tgmlib.TGMLibInstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Adapted from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
public class WyvtilsLoadingPlugin implements IFMLLoadingPlugin {

    public WyvtilsLoadingPlugin() {
        if (!KotlinVersion.CURRENT.isAtLeast(1, 5, 0)) {
            showMessage(new File(new File(KotlinVersion.class.getProtectionDomain().getCodeSource().getLocation().toString()).getParentFile().getParentFile().getName()));
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        TGMLibInstaller.ReturnValue tgmLibInitialized = TGMLibInstaller.initialize(Launch.minecraftHome);
        if (tgmLibInitialized != TGMLibInstaller.ReturnValue.SUCCESSFUL)
            System.out.println("Failed to load TGMLib.");
        else
            System.out.println("Loaded TGMLib successfully.");

        if (TGMLibInstaller.isLoaded())
            return new String[] {"xyz.matthewtgm.tgmlib.tweaker.TGMLibClassTransformer"};
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private void showMessage(File file) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // This makes the JOptionPane show on taskbar and stay on top
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JButton discordLink = new JButton("Join the Discord");
        discordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI("https://discord.gg/b6twapxC3T"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Icon icon = null;
        try {
            URL url = WyvtilsLoadingPlugin.class.getResource("/assets/wyvtils/wyvest.png");
            if (url != null) {
                icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url).getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton close = new JButton("Close");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                exit();
            }
        });

        Object[] options = new Object[]{discordLink, close};
        JOptionPane.showOptionDialog(
                frame,
                "<html><p>Wyvtilities has detected a mod with an older version of Kotlin.<br>The culprit is " + file.toString() + ".<br>It packages version " + KotlinVersion.CURRENT + ".<br>In order to resolve this conflict you must make Wyvtilities be<br>above this mod alphabetically in your mods folder.<br>This tricks Forge into loading Wyvtilities first.<br>You can do this by renaming your Wyvtilities jar to !Wyvtilities.jar,<br>or by renaming the other mod's jar to start with a Z.<br>If you have already done this and are still getting this error,<br>ask for support in the Discord.</p></html>",
                "Wyvtilities Error",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                icon,
                options,
                options[0]
        );
        exit();
    }

    /**
     * Bypasses forges security manager to exit the jvm
     */
    private void exit() {
        try {
            Class<?> clazz = Class.forName("java.lang.Shutdown");
            Method m_exit = clazz.getDeclaredMethod("exit", int.class);
            m_exit.setAccessible(true);
            m_exit.invoke(null, 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
