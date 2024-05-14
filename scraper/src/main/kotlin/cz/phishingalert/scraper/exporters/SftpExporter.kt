package cz.phishingalert.scraper.exporters

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException
import cz.phishingalert.scraper.configuration.AppConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

/**
 * Sends the given data through the SFTP connection
 */
class SftpExporter(val config: AppConfig.SftpConfig): AutoCloseable {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private lateinit var session: Session

    fun setup(): ChannelSftp {
        val jsch = JSch()
        jsch.setKnownHosts("/home/jirik/.ssh/known_hosts")

        session = jsch.getSession(config.username, config.remoteHostName, config.remotePort)
        session.setPassword(config.password)
        session.connect()
        return session.openChannel("sftp") as ChannelSftp
    }

    /**
     * Transfer all data from [from] directory to [to] directory
     */
    fun sendDataFromDir(from: String, to: String) {
        val channel = setup()

        try {
            channel.connect()
            val localDirectory = File(from)

            // Create directory inside [to] directory and enter it
            channel.cd(to)
            channel.mkdir(localDirectory.name)
            channel.cd(localDirectory.name)

            sendDataFromDir(channel, from, localDirectory.name)
        } catch (ex: JSchException) {
            logger.error("Problem when sending data from $from to $to via SFTP, ${ex.message}")
        } catch (ex: SftpException) {
            logger.error(ex.message)
        } finally {
            channel.exit()
        }

    }

    /**
     * Transfer all data from [from] directory to [to] directory via [channel], including subdirectories (recursive)
     */
    fun sendDataFromDir(channel: ChannelSftp, from: String, to: String) {
        val localDirectory = File(from)
        val allItems = localDirectory.listFiles()
        if (allItems == null)  {
            logger.error("Problem with $from directory")
            return
        }

        for (item in allItems) {
            if (item.isDirectory) {
                channel.mkdir(item.name)
                channel.cd(item.name)
                sendDataFromDir(channel, item.absolutePath, localDirectory.resolve(item.name).toString())
                channel.cd("..")
            } else {
                channel.put(item.absolutePath, item.name)
            }
        }
    }

    override fun close() {
        session.disconnect()
    }

}