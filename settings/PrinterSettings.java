package com.kijinseija.seija_printer.settings;

import com.kijinseija.seija_printer.print_main.modules.ClientModule;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.settings.core.Settings;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loads and stores printer settings independently of a host UI. */
public final class PrinterSettings {
    private static final PrinterSettings INSTANCE = new PrinterSettings();
    private static final String FILE_NAME = "seija-printer.json";

    private PrinterSettings() {
    }

    public static PrinterSettings getINSTANCE() {
        return INSTANCE;
    }

    public void load(Settings settings) {
        load(settings, FILE_NAME);
    }

    public void loadModule(ClientModule module) {
        if (module == null) return;
        load(module.settings, fileName(module));
    }

    private void load(Settings settings, String fileName) {
        Path path = configPath(fileName);
        if (!Files.isRegularFile(path)) return;
        try {
            settings.fromJsonString(Files.readString(path, StandardCharsets.UTF_8));
            settings.onModuleActivated();
        } catch (IOException ignored) {
            // A malformed or inaccessible optional config must not prevent the client from loading.
        }
    }

    public void save(Settings settings) {
        save(settings, FILE_NAME);
    }

    public void saveModule(ClientModule module) {
        if (module == null) return;
        save(module.settings, fileName(module));
    }

    public void saveModules(Iterable<? extends ClientModule> modules) {
        if (modules == null) return;
        for (ClientModule module : modules) saveModule(module);
    }

    private void save(Settings settings, String fileName) {
        Path path = configPath(fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, settings.toJsonString(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Configuration is best effort; gameplay must continue when the file is read-only.
        }
    }

    private static String fileName(ClientModule module) {
        if (module instanceof Printer) return FILE_NAME;
        String slug = module.name.toLowerCase(java.util.Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-|-$)", "");
        if (slug.isBlank()) slug = "module";
        return "seija-printer-" + slug + ".json";
    }

    private Path configPath(String fileName) {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(fileName);
    }
}