package dev.threeadd.packeteventssk.test.platform;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.threeadd.packeteventssk.test.utils.TestResults;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.net.HttpURLConnection;

public class Environment {

    private static final Gson gson = new Gson();

    private final String name;

    public String getName() { return name; }

    public static class Resource {
        private final String source;
        private final String target;

        public Resource(String source, String target) {
            this.source = source;
            this.target = target;
        }

        public String getSource() { return source; }
        public String getTarget() { return target; }
    }

    public static class SkriptResource extends Resource {
        private final String version;
        private transient String resolvedSource;

        public SkriptResource(String version, String target) {
            super(null, target);
            this.version = version;
        }

        @Override
        public String getSource() {
            if (resolvedSource == null)
                resolvedSource = "https://repo.skriptlang.org/releases/com/github/SkriptLang/Skript/" + version + "/Skript-" + version + ".jar";
            return resolvedSource;
        }
    }

    public static class PaperResource extends Resource {
        private final String version;
        private transient String resolvedSource;

        public PaperResource(String version, String target) {
            super(null, target);
            this.version = version;
        }

        @Override
        public String getSource() {
            try {
                generateSource();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (resolvedSource == null)
                throw new IllegalStateException();
            return resolvedSource;
        }

        private void generateSource() throws IOException {
            if (resolvedSource != null)
                return;

            String stringUrl = "https://fill.papermc.io/v3/projects/paper/versions/" + version + "/builds";
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "PacketEventsSK-TestPlatform/1.0 (https://github.com/threeadd/PacketEventsSK)");

            JsonArray buildsArray;
            try (InputStream is = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                buildsArray = gson.fromJson(reader, JsonArray.class);
            }

            String stableUrl = null;
            String experimentalUrl = null;

            for (JsonElement element : buildsArray) {
                JsonObject build = element.getAsJsonObject();
                String channel = build.get("channel").getAsString();
                String downloadUrl = build.getAsJsonObject("downloads")
                        .getAsJsonObject("server:default")
                        .get("url").getAsString();
                if ("STABLE".equals(channel)) {
                    stableUrl = downloadUrl;
                    break;
                } else if (experimentalUrl == null) {
                    experimentalUrl = downloadUrl;
                }
            }

            resolvedSource = stableUrl != null ? stableUrl : experimentalUrl;

            if (resolvedSource == null)
                throw new IllegalStateException("No builds found for version " + version);
        }
    }

    private final List<Resource> resources;

    @Nullable
    private final List<Resource> downloads;

    @Nullable
    private final List<PaperResource> paperDownloads;

    @Nullable
    private final List<SkriptResource> skriptDownloads;

    private final String packetEventsSKTarget;

    private final String[] commandLine;

    public Environment(String name, List<Resource> resources, @Nullable List<Resource> downloads,
                       @Nullable List<PaperResource> paperDownloads, @Nullable List<SkriptResource> skriptDownloads,
                       String packetEventsSKTarget, String... commandLine) {
        this.name = name;
        this.resources = resources;
        this.downloads = downloads;
        this.paperDownloads = paperDownloads;
        this.skriptDownloads = skriptDownloads;
        this.packetEventsSKTarget = packetEventsSKTarget;
        this.commandLine = commandLine;
    }

    public void initialize(Path dataRoot, Path runnerRoot) throws IOException {
        Path env = runnerRoot.resolve(name);
        Path plugin = env.resolve(packetEventsSKTarget);
        Files.createDirectories(plugin.getParent());

        // Copy built plugin jar
        Path buildLibsDir = Paths.get("build", "libs");
        File[] jarFiles = buildLibsDir.toFile().listFiles((dir, n) ->
                n.endsWith(".jar") && !n.endsWith("-JUnit.jar"));
        if (jarFiles == null || jarFiles.length == 0)
            throw new RuntimeException("No JAR files found in build/libs!");
        Files.copy(jarFiles[0].toPath(), plugin, StandardCopyOption.REPLACE_EXISTING);

        // Copy resources
        for (Resource resource : resources) {
            Path source = dataRoot.resolve(resource.getSource());
            Path target = env.resolve(resource.getTarget());
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // Download everything
        List<Resource> allDownloads = new ArrayList<>();
        if (this.downloads != null) allDownloads.addAll(this.downloads);
        if (this.paperDownloads != null) allDownloads.addAll(this.paperDownloads);
        if (this.skriptDownloads != null) allDownloads.addAll(this.skriptDownloads);

        for (Resource resource : allDownloads) {
            String source = resource.getSource();
            URL url = new URL(source);
            Path target = env.resolve(resource.getTarget());
            Files.createDirectories(target.getParent());
            System.out.println("Downloading " + source);
            try (InputStream is = url.openStream()) {
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Nullable
    public TestResults runTests(Path runnerRoot, Path testsRoot, String verbosity,
                                long timeout, Set<String> jvmArgs) throws IOException, InterruptedException {
        Path env = runnerRoot.resolve(name);
        Path resultsPath = env.resolve("test_results.json");
        Files.deleteIfExists(resultsPath);

        List<String> args = new ArrayList<>();
        args.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        args.add("-ea");
        args.add("-Dskript.testing.enabled=true");
        args.add("-Dskript.testing.dir=" + testsRoot);
        if (!verbosity.equalsIgnoreCase("null"))
            args.add("-Dskript.testing.verbosity=" + verbosity);
        args.add("-Dskript.testing.results=test_results.json");
        args.add("-Ddisable.watchdog=true");
        args.addAll(jvmArgs);
        args.addAll(Arrays.asList(commandLine));

        Process process = new ProcessBuilder(args)
                .directory(env.toFile())
                .redirectOutput(Redirect.INHERIT)
                .redirectError(Redirect.INHERIT)
                .redirectInput(Redirect.INHERIT)
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

        if (timeout > 0) {
            new Timer("runner watchdog", true).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (process.isAlive()) {
                        System.err.println("Test environment is taking too long, failing...");
                        System.exit(1);
                    }
                }
            }, timeout);
        }

        int code = process.waitFor();
        if (code != 0)
            throw new IOException("environment returned with code " + code);

        if (!Files.exists(resultsPath))
            return null;
        TestResults results = new Gson().fromJson(new String(Files.readAllBytes(resultsPath)), TestResults.class);
        assert results != null;
        return results;
    }
}