package jenkins.plugins.coverity;

import hudson.*;
import hudson.model.*;
import hudson.remoting.Channel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Tom
 * Date: 4/06/11
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
@Extension
public class CoverityLauncherDecorator extends LauncherDecorator {

    public static ThreadLocal<Boolean> SKIP = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public Launcher decorate(Launcher launcher, Node node) {
        Executor executor = Executor.currentExecutor();
        if (executor == null) {
            return launcher;
        }

        Queue.Executable exec = executor.getCurrentExecutable();
        if (!(exec instanceof AbstractBuild)) {
            return launcher;
        }
        AbstractBuild build = (AbstractBuild) exec;

        AbstractProject project = build.getProject();

        CoverityPublisher publisher = (CoverityPublisher) project.getPublishersList().get(CoverityPublisher.class);
        if (publisher == null) {
            return launcher;
        }

        if (publisher.getInvocationAssistance() == null) {
            return launcher;
        }

        try {
            FilePath coverityDir = node.getRootPath().child("coverity");
            coverityDir.mkdirs();
            FilePath temp = coverityDir.createTempDir("temp-", null);

            build.addAction(new CoverityTempDir(temp));

            return new DecoratedLauncher(launcher, "cov-build.exe", "--dir", temp.getRemote());
        } catch (IOException e) {
            throw new RuntimeException("Error while creating temporariy directory for Coverity", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while creating temporariy directory for Coverity");
        }

    }

    /**
     * Returns a decorated {@link Launcher} that puts the given set of arguments as a prefix to any commands
     * that it invokes.
     *
     * @since 1.299
     */
    public class DecoratedLauncher extends Launcher {
        private final Launcher decorated;
        private final String[] prefix;

        public DecoratedLauncher(Launcher decorated, String... prefix) {
            super(decorated);
            this.decorated = decorated;
            this.prefix = prefix;
        }

            @Override
            public Proc launch(ProcStarter starter) throws IOException {
                if (!SKIP.get()) {
                    List<String> cmds = starter.cmds();
                    cmds.addAll(0, Arrays.asList(prefix));
                    starter.cmds(cmds);
                    boolean[] masks = starter.masks();
                    if (masks == null) masks = new boolean[cmds.size()];
                    starter.masks(prefix(masks));

                    starter.envs("COVERITY_UNSUPPORTED=1");
                }
                return decorated.launch(starter);
            }

            @Override
            public Channel launchChannel(String[] cmd, OutputStream out, FilePath workDir, Map<String, String> envVars) throws IOException, InterruptedException {
                return decorated.launchChannel(prefix(cmd),out,workDir,envVars);
            }

            @Override
            public void kill(Map<String, String> modelEnvVars) throws IOException, InterruptedException {
                decorated.kill(modelEnvVars);
            }

            private String[] prefix(String[] args) {
                String[] newArgs = new String[args.length+prefix.length];
                System.arraycopy(prefix,0,newArgs,0,prefix.length);
                System.arraycopy(args,0,newArgs,prefix.length,args.length);
                return newArgs;
            }

            private boolean[] prefix(boolean[] args) {
                boolean[] newArgs = new boolean[args.length+prefix.length];
                System.arraycopy(args,0,newArgs,prefix.length,args.length);
                return newArgs;
            }
        }

}
