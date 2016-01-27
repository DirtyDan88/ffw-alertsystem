# ffw-alertsystem

The ffw-alertsystem provides applications to decode and process POCSAG-telegrams
in order to steer and trigger configurable activities, which are managed by the
plugin-based ffw-alertmonitor.
For further descriptions and usage see in-file comments. Please take also note
of the legal information and restrictions, outlined in LICENSE.



## Contains

### Core applications:
 * alertreceiver
 * alertmonitor
 * watchdog

### MonitorPlugins:
 * AlertActionExecuter
 * DatabaseWriter
 * MessageListener (connects monitor and receiver)
 * MessageLogger
 * TestAlerter
 * WatchdogResetter
 * WebInterface
 * 2 ExamplePlugins

### AlertActions:
 * AlertMailInformer
 * AlertSMSInformer
 * AlertSpeaker
 * HTMLBuilder
 * SystemHibernate
 * TVSwitchOn

### Tools:
 * alertrecv-script
 * arduino-IR-module
 * ...



## Build

### Requirements:
 * Needs jdk 1.8
 * Build tool: Apache ant
 * Should run on all debian-based systems
 * Core applications dependencies:
   * jcommander-1.48.jar
   * javax.mail.jar
   * Additional for alertreceiver: jetty-websockets 9.x
 * For external dependencies of plugins see their buildfiles

### Steps
 * Run build-all.sh will build all core applications and plugins
 * to build receiver: src/core/build-receiver.sh
 * to build monitor: src/core/build-monitor.sh
 * to build watchdog: src/core/build-watchdog.sh
 * to build plugins:
   * monitor-plugins: src/plugins/build-monitorplugins/build-PLUGIN_NAME-plugin.sh
   * alert-actions: src/plugins/build-alertactions/build-ACTION_NAME-action.sh



## Run

### Core applications:
 * alertmonitor: run-alertmonitor {start|stop|debug}
 * alertreceiver: run-alertreceiver {start|stop|debug}
 * watchdog: run-watchdog {start|stop|debug}

### Plugins:
 * MonitorPlugins and AlertActions run as part of the alertmonitor-application
   and will be configurated in XML-file, see example-config-files.

