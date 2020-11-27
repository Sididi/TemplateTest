package com.redpills.correction.framework.helpers

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.GitCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.util.FS
import java.io.File
import java.util.*

object Git {
    val GIT_URI_PATTERN = Regex("^(https|git)(://|@)([^/:]+)[/:]([^/:]+)/(.+).git\$")

    enum class GitUriElement(val index: Int) {
        PROTOCOL(1),
        DOMAIN(3),
        OWNER(4),
        PROJECT_NAME(5)
    }

    data class GitURI(
        val uri: String,
        val protocol: String,
        val domain: String,
        val owner: String,
        val projectName: String
    )

    fun installOrUpdate(repository: GitRepository): File {
        val uri = parseUri(repository)
        val localDirectory = File("student")


        val git = when (localDirectory.exists() && localDirectory.isDirectory) {
            false -> Git.cloneRepository()
                    .setURI(repository.url)
                    .setDirectory(localDirectory)
                    .configure(uri, repository.publicKey)
                    .call() as Git
            true -> {
                val git = Git.open(localDirectory)
                git.fetch().configure(uri, repository.publicKey).call()
                git.pull().configure(uri, repository.publicKey).call()

                git
            }
        }

        git.close()
        return localDirectory
    }

    private fun TransportCommand<out GitCommand<*>, *>.configure(uri: GitURI, sshKey: String) = when(uri.protocol) {
        "git"   -> configureSsh(
            createTempIdentityFile(
                sshKey
            )
        )
        "https" -> configureHttp()
        else    -> error("Invalid repository URL (please use https or git protocol")
    }

    private fun createTempIdentityFile(content: String): String {
        val temp = File.createTempFile(UUID.randomUUID().toString(), null)

        temp.writeText(content)
        temp.deleteOnExit()

        return temp.absolutePath
    }

    private fun TransportCommand<*, *>.configureSsh(sshKey: String) = setTransportConfigCallback {
        (it as SshTransport).sshSessionFactory =
                object : JschConfigSessionFactory() {
                    override fun createDefaultJSch(fs: FS?): JSch {
                        JSch.setConfig("StrictHostKeyChecking", "no")
                        val instance = super.createDefaultJSch(fs)

                        instance.removeAllIdentity()
                        instance.addIdentity(sshKey)
                        return instance;
                    }

                    override fun configure(host: OpenSshConfig.Host?, session: Session?) {
                        session?.userInfo = object : UserInfo {
                            override fun promptPassphrase(message: String?): Boolean = false
                            override fun getPassphrase(): String? = null
                            override fun getPassword(): String? = null
                            override fun promptYesNo(message: String?): Boolean = false
                            override fun showMessage(message: String?) = Unit
                            override fun promptPassword(message: String?): Boolean = false
                        }
                    }
                }
        }

    private fun TransportCommand<*, *>.configureHttp() = setCredentialsProvider(CredentialsProvider.getDefault())

    private fun parseUri(repository: GitRepository): GitURI {
        val uriGroups = GIT_URI_PATTERN.find(repository.url)?.groupValues
                ?: error("Invalid git URI <(${repository.url}>")

        return GitURI(
            repository.url,
            uriGroups[GitUriElement.PROTOCOL.index],
            uriGroups[GitUriElement.DOMAIN.index],
            uriGroups[GitUriElement.OWNER.index],
            uriGroups[GitUriElement.PROJECT_NAME.index]
        )
    }
}