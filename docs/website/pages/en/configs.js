
const React = require("react");
const CompLibrary = require("../../core/CompLibrary.js");
const {MarkdownBlock, GridBlock, Container} = CompLibrary;

class Configs extends React.Component {

    render() {
        return (
            <div className="configs-wrapper"><h1>Twister2 Configurations</h1>

<h2>Common configurations</h2>
<h3>Checkpoint Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.checkpointing.enable</td><td>false</td><td>Enable or disable checkpointing</td></tr><tr><td>twister2.checkpointing.store</td><td>edu.iu.dsc.tws.checkpointing.stores.LocalFileStateStore</td><td>The implementation of the store to be used</td></tr><tr><td>twister2.checkpointing.store.fs.dir</td><td>${'{TWISTER2_HOME}'}/persistent/</td><td>Root directory of local file system based store</td></tr><tr><td>twister2.checkpointing.store.hdfs.dir</td><td>/twister2/persistent/</td><td>Root directory of hdfs based store</td></tr><tr><td>twister2.checkpointing.source.frequency</td><td>1000</td><td>Source triggering frequency</td></tr></tbody></table>

<h3>Data Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.data.hadoop.home</td><td>${'{HADOOP_HOME}'}</td><td></td></tr><tr><td>twister2.data.hdfs.config.directory</td><td>${'{HADOOP_HOME}'}/etc/hadoop/core-site.xml</td><td></td></tr><tr><td>twister2.data.hdfs.data.directory</td><td>${'{HADOOP_HOME}'}/etc/hadoop/hdfs-site.xml</td><td></td></tr><tr><td>twister2.data.hdfs.namenode</td><td>namenode.domain.name</td><td></td></tr><tr><td>twister2.data.hdfs.namenode.port</td><td>9000</td><td></td></tr><tr><td>twister2.data.fs.root</td><td>${'{TWISTER2_HOME}'}/persistent/data</td><td></td></tr><tr><td>twister2.data.hdfs.root</td><td>/twister2/persistent/data</td><td></td></tr></tbody></table>

<h3>Network Configurations</h3>



<h3></h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.buffer.size</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count</td><td>4</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count</td><td>4</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.channel.pending.size</td><td>2048</td><td>channel pending messages</td></tr><tr><td>twister2.network.send.pending.max</td><td>4</td><td>the send pending messages</td></tr><tr><td>twister2.network.message.group.low_water_mark</td><td>8</td><td>group up to 8 ~ 16 messages</td></tr><tr><td>twister2.network.message.group.high_water_mark</td><td>16</td><td>this is the max number of messages to group</td></tr><tr><td>twister2.network.message.grouping.size</td><td>10</td><td>in batch partition operations, this value will be used to create mini batches<br/>within partial receivers</td></tr><tr><td>twister2.network.ops.persistent.dirs</td><td>["${'{TWISTER2_HOME}'}/persistent/"]</td><td>For disk based operations, this directory list will be used to persist incoming messages.<br/>This can be used to balance the load between multiple devices, by specifying directory locations<br/>from different devices.</td></tr><tr><td>twister2.network.shuffle.memory.bytes.max</td><td>102400000</td><td>the maximum amount of bytes kept in memory for operations that goes to disk</td></tr><tr><td>twister2.network.shuffle.memory.records.max</td><td>102400000</td><td>the maximum number of records kept in memory for operations that goes to dist</td></tr><tr><td>twister2.network.shuffle.file.bytes.max</td><td>10000000</td><td>size of the shuffle file (10MB default)</td></tr><tr><td>twister2.network.shuffle.parallel.io</td><td>2</td><td>no of parallel IO operations permitted</td></tr><tr><td>twister2.network.alltoall.algorithm.batch</td><td>ring</td><td>the partitioning algorithm</td></tr></tbody></table>

<h3></h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.buffer.size.stream.reduce</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.stream.reduce</td><td>2</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.strea.reduce</td><td>2</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.buffer.size.stream.gather</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.stream.gather</td><td>2</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.stream.gather</td><td>2</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.buffer.size.stream.bcast</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.stream.bcast</td><td>2</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.stream.bcast</td><td>2</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.alltoall.algorithm.stream.partition</td><td>simple</td><td>the partitioning algorithm</td></tr><tr><td>twister2.network.buffer.size.stream.partition</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.stream.partition</td><td>4</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.stream.partition</td><td>4</td><td>number of receive buffers to be used</td></tr></tbody></table>

<h3></h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.buffer.size.batch.reduce</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.batch.reduce</td><td>2</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.batch.reduce</td><td>2</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.buffer.size.batch.gather</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.batch.gather</td><td>2</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.batch.gather</td><td>2</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.buffer.size.batch.bcast</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.batch.bcast</td><td>2</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.batch.bcast</td><td>2</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.alltoall.algorithm.batch.partition</td><td>simple</td><td>the partitioning algorithm</td></tr><tr><td>twister2.network.buffer.size.batch.partition</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.batch.partition</td><td>4</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.batch.partition</td><td>4</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.alltoall.algorithm.batch.keyed_gather</td><td>simple</td><td>the partitioning algorithm</td></tr><tr><td>ttwister2.network.partition.ring.group.workers.batch.keyed_gather</td><td>2</td><td>ring group worker</td></tr><tr><td>twister2.network.buffer.size.batch.keyed_gather</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.batch.keyed_gather</td><td>4</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.batch.keyed_gather</td><td>4</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.network.message.group.low_water_mark.batch.keyed_gather</td><td>8000</td><td>group up to 8 ~ 16 messages</td></tr><tr><td>twister2.network.message.group.high_water_mark.batch.keyed_gather</td><td>16000</td><td>this is the max number of messages to group</td></tr><tr><td>twister2.network.message.grouping.size.batch.keyed_gather</td><td>10000</td><td>in batch partition operations, this value will be used to create mini batches<br/>within partial receivers</td></tr><tr><td>twister2.network.alltoall.algorithm.batch.keyed_reduce</td><td>simple</td><td>the partitioning algorithm</td></tr><tr><td>ttwister2.network.partition.ring.group.workers.batch.keyed_reduce</td><td>2</td><td>ring group worker</td></tr><tr><td>twister2.network.buffer.size.batch.keyed_reduce</td><td>1024000</td><td>the buffer size to be used</td></tr><tr><td>twister2.network.sendBuffer.count.batch.keyed_reduce</td><td>4</td><td>number of send buffers to be used</td></tr><tr><td>twister2.network.receiveBuffer.count.batch.keyed_reduce</td><td>4</td><td>number of receive buffers to be used</td></tr><tr><td>twister2.python.port</td><td>5400</td><td>port offset for python-java connection.<br/>port+workerId will be used by each worker to communicate with python process<br/>port-1 will be used by client process for the initial communication</td></tr></tbody></table>

<h3>Resource Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.client.debug</td><td>'-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5006'</td><td>use this property to debug the client submitting the job</td></tr><tr><td>twister2.resource.systempackage.copy</td><td>false</td><td>Weather we have a requirement to copy the system package</td></tr></tbody></table>

<h3>ZooKeeper related config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.zookeeper.based.group.management</td><td>true</td><td>ZooKeeper can be used to exchange job status data and discovery<br/>Workers can discover one another through ZooKeeper<br/>They update their status on ZooKeeper<br/>Dashboard can get job events through ZooKeeper<br/>If fault tolerance is enabled, ZooKeeper is used, irrespective of this parameter</td></tr><tr><td>#twister2.resource.zookeeper.server.addresses</td><td>ip:port</td><td>when conf/kubernetes/deployment/zookeeper-wo-persistence.yaml is used<br/>following service name can be used as zk address<hr/><b>Options</b><ul><li>127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002</li></ul></td></tr><tr><td>twister2.zookeeper.root.node.path</td><td>/twister2</td><td>the root node path of this job on ZooKeeper<br/>the default is "/twister2"</td></tr></tbody></table>

<h3>Core Configurations</h3>



<h3>User name that will be used in JobID<br/>JobID is constructed as:<br/>  [username]-[jobName]-[timestamp]<br/>if username is specified here, we use this value.<br/>Otherwise we get username from shell environment.<br/>if the username is longer than 9 characters, we use first 9 characters of it<br/>long value of timestamp is converted to alphanumeric string format<br/>timestamp long value = current time - 01/01/2019<br/>twister2.user.name:</h3>
<h3>Twister2 Job Master related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.job.master.used</td><td>true</td><td></td></tr><tr><td>twister2.job.master.runs.in.client</td><td>true</td><td>if true, the job master runs in the submitting client<br/>if false, job master runs as a separate process in the cluster<br/>by default, it is true<br/>when the job master runs in the submitting client, this client has to be submitting the job from a machine in the cluster</td></tr><tr><td>twister2.worker.to.job.master.response.wait.duration</td><td>100000</td><td></td></tr></tbody></table>

<h3>WorkerController related config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.worker.controller.max.wait.time.for.all.workers.to.join</td><td>100000</td><td>amount of timeout for all workers to join the job<br/>in milli seconds</td></tr><tr><td>twister2.worker.controller.max.wait.time.on.barrier</td><td>100000</td><td>amount of timeout on barriers for all workers to arrive<br/>in milli seconds</td></tr></tbody></table>

<h3>Common thread pool config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.common.thread.pool.threads</td><td>2</td><td>Maximum number of threads to spawn on demand</td></tr><tr><td>twister2.common.thread.pool.keepalive</td><td>10</td><td>maximum time that excess idle threads will wait for new tasks before terminating</td></tr><tr><td>twister.python.bin</td><td>python3</td><td>path to python binary</td></tr></tbody></table>

<h3>Dashboard related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.dashboard.host</td><td>http://localhost:8080</td><td>Dashboard server host address and port<br/>if this parameter is not specified, then job master will not try to connect to Dashboard</td></tr></tbody></table>

<h3>Task Configurations</h3>



<h3>Task Scheduler Related Configurations</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.taskscheduler.streaming</td><td>roundrobin</td><td>Task scheduling mode for the streaming jobs "roundrobin" or "firstfit" or "datalocalityaware" or "userdefined"</td></tr><tr><td>twister2.taskscheduler.streaming.class</td><td>edu.iu.dsc.tws.tsched.streaming.roundrobin.RoundRobinTaskScheduler</td><td>Task Scheduler class for the round robin streaming task scheduler</td></tr><tr><td>#twister2.taskscheduler.streaming.class</td><td>edu.iu.dsc.tws.tsched.streaming.datalocalityaware.DataLocalityStreamingTaskScheduler</td><td>Task Scheduler for the Data Locality Aware Streaming Task Scheduler</td></tr><tr><td>#twister2.taskscheduler.streaming.class</td><td>edu.iu.dsc.tws.tsched.streaming.firstfit.FirstFitStreamingTaskScheduler</td><td>Task Scheduler for the FirstFit Streaming Task Scheduler</td></tr><tr><td>#twister2.taskscheduler.streaming.class</td><td>edu.iu.dsc.tws.tsched.userdefined.UserDefinedTaskScheduler</td><td>Task Scheduler for the userDefined Streaming Task Scheduler</td></tr><tr><td>twister2.taskscheduler.batch</td><td>batchscheduler</td><td>Task scheduling mode for the batch jobs "roundrobin" or "datalocalityaware" or "userdefined"</td></tr><tr><td>#twister2.taskscheduler.batch.class</td><td>edu.iu.dsc.tws.tsched.batch.roundrobin.RoundRobinBatchTaskScheduler</td><td>Task Scheduler class for the round robin batch task scheduler</td></tr><tr><td>twister2.taskscheduler.batch.class</td><td>edu.iu.dsc.tws.tsched.batch.batchscheduler.BatchTaskScheduler</td><td>Task Scheduler class for the batch task scheduler</td></tr><tr><td>#twister2.taskscheduler.batch.class</td><td>edu.iu.dsc.tws.tsched.batch.datalocalityaware.DataLocalityBatchTaskScheduler</td><td>Task Scheduler for the Data Locality Aware Batch Task Scheduler</td></tr><tr><td>#twister2.taskscheduler.batch.class</td><td>edu.iu.dsc.tws.tsched.userdefined.UserDefinedTaskScheduler</td><td>Task Scheduler for the userDefined Batch Task Scheduler</td></tr><tr><td>twister2.taskscheduler.task.instances</td><td>2</td><td>Number of task instances to be allocated to each worker/container</td></tr><tr><td>twister2.taskscheduler.task.instance.ram</td><td>512.0</td><td>Ram value to be allocated to each task instance</td></tr><tr><td>twister2.taskscheduler.task.instance.disk</td><td>500.0</td><td>Disk value to be allocated to each task instance</td></tr><tr><td>twister2.taskscheduler.task.instance.cpu</td><td>2.0</td><td>CPU value to be allocated to each task instance</td></tr><tr><td>twister2.taskscheduler.container.instance.ram</td><td>4096.0</td><td>Default Container Instance Values<br/>Ram value to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.container.instance.disk</td><td>8000.0</td><td>Disk value to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.container.instance.cpu</td><td>16.0</td><td>CPU value to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.ram.padding.container</td><td>2.0</td><td>Default Container Padding Values<br/>Default padding value of the ram to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.disk.padding.container</td><td>12.0</td><td>Default padding value of the disk to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.cpu.padding.container</td><td>1.0</td><td>CPU padding value to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.container.padding.percentage</td><td>2</td><td>Percentage value to be allocated to each container</td></tr><tr><td>twister2.taskscheduler.container.instance.bandwidth</td><td>100 #Mbps</td><td>Static Default Network parameters<br/>Bandwidth value to be allocated to each container instance for datalocality scheduling</td></tr><tr><td>twister2.taskscheduler.container.instance.latency</td><td>0.002 #Milliseconds</td><td>Latency value to be allocated to each container instance for datalocality scheduling</td></tr><tr><td>twister2.taskscheduler.datanode.instance.bandwidth</td><td>200 #Mbps</td><td>Bandwidth to be allocated to each datanode instance for datalocality scheduling</td></tr><tr><td>twister2.taskscheduler.datanode.instance.latency</td><td>0.01 #Milliseconds</td><td>Latency value to be allocated to each datanode instance for datalocality scheduling</td></tr><tr><td>twister2.taskscheduler.task.parallelism</td><td>2</td><td>Prallelism value to each task instance</td></tr><tr><td>twister2.taskscheduler.task.type</td><td>streaming</td><td>Task type to each submitted job by default it is "streaming" job.</td></tr><tr><td>twister2.exector.worker.threads</td><td>1</td><td>number of threads per worker</td></tr><tr><td>twister2.executor.batch.name</td><td>edu.iu.dsc.tws.executor.threading.BatchSharingExecutor2</td><td>name of the batch executor</td></tr><tr><td>twister2.exector.instance.queue.low.watermark</td><td>10000</td><td>number of tuples executed at a single pass</td></tr><tr><td>twister2.executor.stream.name</td><td>edu.iu.dsc.tws.executor.threading.StreamingSharingExecutor</td><td>name of the streaming executor</td></tr><tr><td>twister2.executor.stream.name</td><td>edu.iu.dsc.tws.executor.threading.StreamingAllSharingExecutor</td><td></td></tr></tbody></table>

<h2>Standalone configurations</h2>
<h3>Checkpoint Configurations</h3>



No specific configurations
<h3>Data Configurations</h3>



No specific configurations
<h3>Network Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.channel.class</td><td>edu.iu.dsc.tws.comms.mpi.TWSMPIChannel</td><td></td></tr></tbody></table>

<h3>Resource Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.scheduler.mpi.working.directory</td><td>${'{HOME}'}/.twister2/jobs</td><td>working directory</td></tr><tr><td>twsiter2.resource.scheduler.mpi.mode</td><td>standalone</td><td>mode of the mpi scheduler</td></tr><tr><td>twister2.resource.scheduler.mpi.job.id</td><td></td><td>the job id file</td></tr><tr><td>twister2.resource.scheduler.mpi.shell.script</td><td>mpi.sh</td><td>slurm script to run</td></tr><tr><td>twister2.resource.scheduler.mpi.home</td><td></td><td>the mpirun command location</td></tr><tr><td>twister2.resource.system.package.uri</td><td>${'{TWISTER2_DIST}'}/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td>the package uri</td></tr><tr><td>twister2.resource.class.launcher</td><td>edu.iu.dsc.tws.rsched.schedulers.standalone.MPILauncher</td><td>the launcher class</td></tr><tr><td>twister2.resource.scheduler.mpi.mpirun.file</td><td>ompi/bin/mpirun</td><td>mpi run file, this assumes a mpirun that is shipped with the product<br/>change this to just mpirun if you are using a system wide installation of OpenMPI<br/>or complete path of OpenMPI in case you have something custom</td></tr><tr><td>twister2.resource.scheduler.mpi.mapby</td><td>node</td><td>mpi scheduling policy. Two possible options are node and slot.<br/>read more at https://www.open-mpi.org/faq/?category=running#mpirun-scheduling</td></tr><tr><td>twister2.resource.scheduler.mpi.mapby.use-pe</td><td>false</td><td>use mpi map-by modifier PE. If this option is enabled, cpu count of compute resource<br/>specified in job definition will be taken into consideration</td></tr><tr><td>twister2.resource.sharedfs</td><td>true</td><td>Indicates whether bootstrap process needs to be run and distribute job file and core<br/>between nodes. Twister2 assumes job file is accessible to all nodes if this property is set<br/>to true, else it will run the bootstrap process</td></tr><tr><td>twister2.resource.fs.mount</td><td>${'{TWISTER2_HOME}'}/persistent/fs/</td><td>Directory for file system volume mount</td></tr><tr><td>twister2.resource.uploader.directory</td><td>${'{HOME}'}/.twister2/repository</td><td>the uploader directory</td></tr><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.localfs.LocalFileSystemUploader</td><td>the uplaoder class</td></tr></tbody></table>

<h3>Core Configurations</h3>



No specific configurations
<h3>Task Configurations</h3>



No specific configurations
<h2>Slurm configurations</h2>
<h3>Checkpoint Configurations</h3>



No specific configurations
<h3>Data Configurations</h3>



No specific configurations
<h3>Network Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.channel.class</td><td>edu.iu.dsc.tws.comms.mpi.TWSMPIChannel</td><td></td></tr></tbody></table>

<h3>Resource Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.scheduler.mpi.working.directory</td><td>${'{HOME}'}/.twister2/jobs</td><td>working directory</td></tr><tr><td>twsiter2.resource.scheduler.mpi.mode</td><td>slurm</td><td>mode of the mpi scheduler</td></tr><tr><td>twister2.resource.scheduler.mpi.job.id</td><td></td><td>the job id file</td></tr><tr><td>twister2.resource.scheduler.mpi.shell.script</td><td>mpi.sh</td><td>slurm script to run</td></tr><tr><td>twister2.resource.scheduler.slurm.partition</td><td>juliet</td><td>slurm partition</td></tr><tr><td>twister2.resource.scheduler.mpi.home</td><td></td><td>the mpirun command location</td></tr><tr><td>twister2.resource.system.package.uri</td><td>${'{TWISTER2_DIST}'}/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td>the package uri</td></tr><tr><td>twister2.resource.class.launcher</td><td>edu.iu.dsc.tws.rsched.schedulers.standalone.MPILauncher</td><td>the launcher class</td></tr><tr><td>twister2.resource.scheduler.mpi.mpirun.file</td><td>twister2-core/ompi/bin/mpirun</td><td>mpi run file, this assumes a mpirun that is shipped with the product<br/>change this to just mpirun if you are using a system wide installation of OpenMPI<br/>or complete path of OpenMPI in case you have something custom</td></tr><tr><td>twister2.resource.uploader.directory</td><td>${'{HOME}'}/.twister2/repository</td><td>the uploader directory</td></tr><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.localfs.LocalFileSystemUploader</td><td>the uplaoder class</td></tr></tbody></table>

<h3>Core Configurations</h3>



<h3>WorkerController related config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.worker.controller.max.wait.time.for.all.workers.to.join</td><td>100000</td><td>amount of timeout for all workers to join the job<br/>in milli seconds</td></tr><tr><td>twister2.worker.controller.max.wait.time.on.barrier</td><td>100000</td><td>amount of timeout on barriers for all workers to arrive<br/>in milli seconds</td></tr></tbody></table>

<h3>Dashboard related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.dashboard.host</td><td>http://localhost:8080</td><td>Dashboard server host address and port<br/>if this parameter is not specified, then job master will not try to connect to Dashboard</td></tr></tbody></table>

<h3>Task Configurations</h3>



No specific configurations
<h2>Aurora configurations</h2>
<h3>Checkpoint Configurations</h3>



No specific configurations
<h3>Data Configurations</h3>



No specific configurations
<h3>Network Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.channel.class</td><td>edu.iu.dsc.tws.comms.tcp.TWSTCPChannel</td><td></td></tr></tbody></table>

<h3>Resource Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.system.package.uri</td><td>${'{TWISTER2_DIST}'}/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td>the package uri</td></tr><tr><td>twister2.resource.class.launcher</td><td>edu.iu.dsc.tws.rsched.schedulers.aurora.AuroraLauncher</td><td>launcher class for aurora submission</td></tr><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.scp.ScpUploader</td><td>the uploader class<hr/><b>Options</b><ul><li>edu.iu.dsc.tws.rsched.uploaders.NullUploader</li><li>edu.iu.dsc.tws.rsched.uploaders.localfs.LocalFileSystemUploader</li></ul></td></tr><tr><td>twister2.resource.job.worker.class</td><td>edu.iu.dsc.tws.examples.internal.rsched.BasicAuroraContainer</td><td>container class to run in workers</td></tr><tr><td>twister2.resource.class.aurora.worker</td><td>edu.iu.dsc.tws.rsched.schedulers.aurora.AuroraWorkerStarter</td><td>the Aurora worker class</td></tr></tbody></table>

<h3>Uploader configuration</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.uploader.directory</td><td>/root/.twister2/repository/</td><td>the directory where the file will be uploaded, make sure the user has the necessary permissions<br/>to upload the file here.</td></tr><tr><td>twister2.resource.uploader.scp.command.options</td><td></td><td>This is the scp command options that will be used by the uploader, this can be used to<br/>specify custom options such as the location of ssh keys.</td></tr><tr><td>twister2.resource.uploader.scp.command.connection</td><td>root@149.165.150.81</td><td>The scp connection string sets the remote user name and host used by the uploader.</td></tr><tr><td>twister2.resource.uploader.ssh.command.options</td><td></td><td>The ssh command options that will be used when connecting to the uploading host to execute<br/>command such as delete files, make directories.</td></tr><tr><td>twister2.resource.uploader.ssh.command.connection</td><td>root@149.165.150.81</td><td>The ssh connection string sets the remote user name and host used by the uploader.</td></tr></tbody></table>

<h3>Client configuration parameters for submission of twister2 jobs</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.scheduler.aurora.script</td><td>${'{TWISTER2_CONF}'}/twister2.aurora</td><td>aurora python script to submit a job to Aurora Scheduler<br/>its default value is defined as the following in the code<br/>can be reset from this config file if desired</td></tr><tr><td>twister2.resource.scheduler.aurora.cluster</td><td>example</td><td>cluster name aurora scheduler runs in</td></tr><tr><td>twister2.resource.scheduler.aurora.role</td><td>www-data</td><td>role in cluster</td></tr><tr><td>twister2.resource.scheduler.aurora.env</td><td>devel</td><td>environment name</td></tr><tr><td>twister2.resource.job.name</td><td>basic-aurora</td><td>aurora job name<hr/><b>Options</b><ul><li>basic-aurora</li></ul></td></tr><tr><td>twister2.resource.worker.cpu</td><td>1.0</td><td>number of cores for each worker<br/>it is a floating point number<br/>each worker can have fractional cores such as 0.5 cores or multiple cores as 2<br/>default value is 1.0 core</td></tr><tr><td>twister2.resource.worker.ram</td><td>200</td><td>amount of memory for each worker in the job in mega bytes as integer<br/>default value is 200 MB</td></tr><tr><td>twister2.resource.worker.disk</td><td>1024</td><td>amount of hard disk space on each worker in mega bytes<br/>this only used when running twister2 in Aurora<br/>default value is 1024 MB.</td></tr><tr><td>twister2.resource.worker.instances</td><td>6</td><td>number of worker instances</td></tr></tbody></table>

<h3>Core Configurations</h3>



No specific configurations
<h3>Task Configurations</h3>



No specific configurations
<h2>Kubernetes configurations</h2>
<h3>Checkpoint Configurations</h3>



No specific configurations
<h3>Data Configurations</h3>



No specific configurations
<h3>Network Configurations</h3>



<h3>OpenMPI settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.channel.class</td><td>edu.iu.dsc.tws.comms.tcp.TWSTCPChannel</td><td>If the channel is set as TWSMPIChannel,<br/>the job is started as OpenMPI job<br/>Otherwise, it is a regular twister2 job. OpenMPI is not started in this case.</td></tr><tr><td>kubernetes.secret.name</td><td>twister2-openmpi-ssh-key</td><td>A Secret object must be present in Kubernetes master<br/>Its name must be specified here</td></tr></tbody></table>

<h3>Worker port settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>kubernetes.worker.base.port</td><td>9000</td><td>the base port number workers will use internally to communicate with each other<br/>when there are multiple workers in a pod, first worker will get this port number,<br/>second worker will get the next port, and so on.<br/>default value is 9000,</td></tr><tr><td>kubernetes.worker.transport.protocol</td><td>TCP</td><td>transport protocol for the worker. TCP or UDP<br/>by default, it is TCP<br/>set if it is UDP</td></tr></tbody></table>

<h3>NodePort service parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>kubernetes.node.port.service.requested</td><td>true</td><td>if the job requests NodePort service, it must be true<br/>NodePort service makes the workers accessible from external entities (outside of the cluster)<br/>by default, its value is false</td></tr><tr><td>kubernetes.service.node.port</td><td>30003</td><td>if NodePort value is 0, it is automatically assigned a value<br/>the user can request a specific port value in the NodePort range by setting the value below<br/>by default Kubernetes uses the range 30000-32767 for NodePorts<br/>Kubernetes admins can change this range</td></tr></tbody></table>

<h3>Resource Configurations</h3>



<h3>Kubernetes Docker Image and related settings<br/>Twister2 Docker image for Kubernetes</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.system.package.uri</td><td>${'{TWISTER2_DIST}'}/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td>the package uri</td></tr><tr><td>twister2.resource.class.launcher</td><td>edu.iu.dsc.tws.rsched.schedulers.k8s.KubernetesLauncher</td><td></td></tr></tbody></table>

<h3>Kubernetes related settings<br/>namespace to use in kubernetes<br/>default value is "default"</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>kubernetes.image.pull.policy</td><td>Always</td><td>image pull policy, by default is IfNotPresent<br/>it could also be Always</td></tr><tr><td>kubernetes.log.in.client</td><td>true</td><td>get log messages to twister2 client and save in files<br/>it is false by default</td></tr><tr><td>kubernetes.check.pods.reachable</td><td>true</td><td>before connecting to other pods in the job,<br/>check whether all pods are reachable from each pod<br/>wait until all pods become reachable<br/>when there are networking issues, pods may not be reachable immediately,<br/>so this makes sure to wait before all pods become reachable<br/>it is false by default</td></tr></tbody></table>

<h3>Job configuration parameters for submission of twister2 jobs<br/>Jobs can be loaded from these configurations or<br/>they can be specified programmatically by using Twister2JobBuilder</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.job.name</td><td>t2-job</td><td>twister2 job name</td></tr><tr><td>    # number of workers using this compute resource</td><td>instances * workersPerPod</td><td>A Twister2 job can have multiple sets of compute resources<br/>instances shows the number of compute resources to be started with this specification<br/>workersPerPod shows the number of workers on each pod in Kubernetes.<br/>   May be omitted in other clusters. default value is 1.</td></tr><tr><td>#- cpu</td><td>0.5  # number of cores for each worker, may be fractional such as 0.5 or 2.4</td><td><hr/><b>Options</b><ul><li>1024 # ram for each worker as Mega bytes</li><li>1.0 # volatile disk for each worker as Giga bytes</li><li>2 # number of compute resource instances with this specification</li><li>false # only one ComputeResource can be scalable in a job</li><li>1 # number of workers on each pod in Kubernetes. May be omitted in other clusters.</li></ul></td></tr><tr><td>twister2.resource.job.driver.class</td><td>edu.iu.dsc.tws.examples.internal.rsched.DriverExample</td><td>driver class to run</td></tr><tr><td>twister2.resource.job.worker.class</td><td>edu.iu.dsc.tws.examples.basic.HelloWorld</td><td>worker class to run<hr/><b>Options</b><ul><li>edu.iu.dsc.tws.examples.internal.BasicNetworkTest</li><li>edu.iu.dsc.tws.examples.comms.batch.BReduceExample</li><li>edu.iu.dsc.tws.examples.internal.BasicNetworkTest</li></ul></td></tr><tr><td>twister2.resource.worker.additional.ports</td><td>["port1", "port2", "port3"]</td><td>by default each worker has one port<br/>additional ports can be requested for all workers in a job<br/>please provide the requested port names as a list such as:</td></tr></tbody></table>

<h3>persistent volume related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.persistent.volume.per.worker</td><td>0.0</td><td>persistent volume size per worker in GB as double<br/>default value is 0.0Gi<br/>set this value to zero, if you have not persistent disk support<br/>when this value is zero, twister2 will not try to set up persistent storage for this job</td></tr><tr><td>twister2.resource.kubernetes.persistent.storage.class</td><td>twister2-nfs-storage</td><td>cluster admin should provide a storage provisioner.<br/>please specify the storage class name that is used by the provisioner<br/>Minikube has a default provisioner with storageClass of "standard"<hr/><b>Options</b><ul><li>standard</li></ul></td></tr><tr><td>twister2.resource.kubernetes.storage.access.mode</td><td>ReadWriteMany</td><td>persistent storage access mode.<br/>It shows the access mode for workers to access the shared persistent storage.<br/>if it is "ReadWriteMany", many workers can read and write<br/>https://kubernetes.io/docs/concepts/storage/persistent-volumes</td></tr></tbody></table>

<h3>K8sUploader Settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.k8s.K8sUploader</td><td></td></tr></tbody></table>

<h3>When a job is submitted, the job package needs to be transferred to worker pods<br/>K8sUploader provides two upload methods:<br/>  a) Transferring job package to job pods from submitting client directly<br/>  b) Transferring job package to web server pods.<br/>     Job pods download the job package from web server pods.<br/>     You need to deploy twister2 uploader pods for this to work.<br/>We first check whether there is any uploader web server running,<br/>if there is, we upload the job package to the uploader web server pods.<br/>Otherwise, we upload the job package to job pods directly from submitting client.</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>it is by default</td><td>http://twister2-uploader.default.svc.cluster.local</td><td>uploader web server address<br/>if you are using, twister2-uploader-wo-ps.yaml<br/>no need to set this parameter, default one is ok<hr/><b>Options</b><ul><li>http://twister2-uploader.default.svc.cluster.local</li></ul></td></tr><tr><td>it is by default</td><td>/usr/share/nginx/html</td><td>uploader web server directory<br/>job package will be uploaded to this directory<br/>if you are using, twister2-uploader-wo-ps.yaml<br/>no need to set this parameter, default one is ok<hr/><b>Options</b><ul><li>/usr/share/nginx/html</li></ul></td></tr><tr><td>it is by default</td><td>app=twister2-uploader</td><td>uploader web server label<br/>job package will be uploaded to the pods that have this label<br/>if you are using, twister2-uploader-wo-ps.yaml<br/>no need to set this parameter, default one is ok<hr/><b>Options</b><ul><li>app=twister2-uploader</li></ul></td></tr></tbody></table>

<h3>S3Uploader Settings<br/>To use S3Uploader:<br/>  Uncomment uploader class below.<br/>  Specify bucket name<br/>  If your job will run more than 2 hours and it is fault tolerant, update link.expiration.duration</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.s3.S3Uploader</td><td></td></tr><tr><td>twister2.s3.bucket.name</td><td>s3://[bucket-name]</td><td>s3 bucket name to upload the job package<br/>workers will download the job package from this location</td></tr><tr><td>twister2.s3.link.expiration.duration.sec</td><td>7200</td><td>job package link will be available this much time<br/>by default, it is 2 hours</td></tr></tbody></table>

<h3>Node locations related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.kubernetes.node.locations.from.config</td><td>false</td><td>If this parameter is set as true,<br/>Twister2 will use the below lists for node locations:<br/>  kubernetes.datacenters.list<br/>  kubernetes.racks.list<br/>Otherwise, it will try to get these information by querying Kubernetes Master<br/>It will use below two labels when querying node locations<br/>For this to work, submitting client has to have admin privileges</td></tr><tr><td>twister2.resource.rack.labey.key</td><td>rack</td><td>rack label key for Kubernetes nodes in a cluster<br/>each rack should have a unique label<br/>all nodes in a rack should share this label<br/>Twister2 workers can be scheduled by using these label values<br/>Better data locality can be achieved<br/>no default value is specified</td></tr><tr><td>twister2.resource.datacenter.labey.key</td><td>datacenter</td><td>data center label key<br/>each data center should have a unique label<br/>all nodes in a data center should share this label<br/>Twister2 workers can be scheduled by using these label values<br/>Better data locality can be achieved<br/>no default value is specified</td></tr><tr><td>  - echo</td><td>['blue-rack', 'green-rack']</td><td>Data center list with rack names</td></tr><tr><td>  - green-rack</td><td>['node11.ip', 'node12.ip', 'node13.ip']</td><td>Rack list with node IPs in them</td></tr></tbody></table>

<h3>Kubernetes Mapping and Binding parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>kubernetes.bind.worker.to.cpu</td><td>true</td><td>Statically bind workers to CPUs<br/>Workers do not move from the CPU they are started during computation<br/>twister2.cpu_per_container has to be an integer<br/>by default, its value is false</td></tr><tr><td>kubernetes.worker.to.node.mapping</td><td>true</td><td>kubernetes can map workers to nodes as specified by the user<br/>default value is false</td></tr><tr><td>twister2.resource.kubernetes.worker.mapping.key</td><td>kubernetes.io/hostname</td><td>the label key on the nodes that will be used to map workers to nodes</td></tr><tr><td>twister2.resource.kubernetes.worker.mapping.operator</td><td>In</td><td>operator to use when mapping workers to nodes based on key value<br/>Exists/DoesNotExist checks only the existence of the specified key in the node.<br/>Ref https://kubernetes.io/docs/concepts/configuration/assign-pod-node/#node-affinity-beta-feature</td></tr><tr><td>twister2.resource.kubernetes.worker.mapping.values</td><td>['e012', 'e013']</td><td>values for the mapping key<br/>when the mapping operator is either Exists or DoesNotExist, values list must be empty.<hr/><b>Options</b><ul><li>[]</li></ul></td></tr><tr><td>Valid values</td><td>all-same-node, all-separate-nodes, none</td><td>uniform worker mapping<br/>default value is none<hr/><b>Options</b><ul><li>all-same-node</li></ul></td></tr></tbody></table>

<h3>Core Configurations</h3>



<h3>Fault Tolerance configurations</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.fault.tolerant</td><td>false</td><td>A flag to enable/disable fault tolerance in Twister2<br/>By default, it is disabled</td></tr><tr><td>twister2.fault.tolerance.failure.timeout</td><td>10000</td><td>a timeout value to determine whether a worker failed<br/>If a worker does not send heartbeat messages for this duration in milli seconds,<br/>It is assumed failed</td></tr></tbody></table>

<h3>Twister2 Job Master related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.job.master.runs.in.client</td><td>false</td><td>if true, the job master runs in the submitting client<br/>if false, job master runs as a separate process in the cluster <br/>by default, it is true<br/>when the job master runs in the submitting client,<br/>this client has to be submitting the job from a machine in the cluster<br/>getLocalHost must return a reachable IP address to the job master</td></tr><tr><td>twister2.job.master.port</td><td>11011</td><td>twister2 job master port number<br/>default value is 11011</td></tr><tr><td>twister2.worker.to.job.master.response.wait.duration</td><td>10000</td><td>worker to job master response wait time in milliseconds<br/>this is for messages that wait for a response from the job master<br/>default value is 10seconds = 10000</td></tr><tr><td>twister2.job.master.volatile.volume.size</td><td>0.0</td><td>twister2 job master volatile volume size in GB<br/>default value is 1.0 Gi<br/>if this value is 0, volatile volume is not setup for job master</td></tr><tr><td>twister2.job.master.persistent.volume.size</td><td>0.0</td><td>twister2 job master persistent volume size in GB<br/>default value is 1.0 Gi<br/>if this value is 0, persistent volume is not setup for job master</td></tr><tr><td>twister2.job.master.cpu</td><td>0.2</td><td>twister2 job master cpu request<br/>default value is 0.2 percentage</td></tr><tr><td>twister2.job.master.ram</td><td>1024</td><td>twister2 job master RAM request in MB<br/>default value is 1024 MB</td></tr></tbody></table>

<h3>WorkerController related config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.worker.controller.max.wait.time.for.all.workers.to.join</td><td>100000</td><td>amount of timeout for all workers to join the job<br/>in milli seconds</td></tr><tr><td>twister2.worker.controller.max.wait.time.on.barrier</td><td>100000</td><td>amount of timeout on barriers for all workers to arrive<br/>in milli seconds</td></tr></tbody></table>

<h3>Dashboard related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.job.master.to.dashboard.connections</td><td>3</td><td>the number of http connections from job master to Twister2 Dashboard<br/>default value is 3<br/>for jobs with large number of workers, this can be set to higher number</td></tr><tr><td>twister2.dashboard.host</td><td>http://twister2-dashboard.default.svc.cluster.local</td><td>Dashboard server host address and port<br/>if this parameter is not specified, then job master will not try to connect to Dashboard<br/>if dashboard is running as a statefulset in the cluster</td></tr></tbody></table>

<h3>Task Configurations</h3>



No specific configurations
<h2>Mesos configurations</h2>
<h3>Checkpoint Configurations</h3>



No specific configurations
<h3>Data Configurations</h3>



No specific configurations
<h3>Network Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.channel.class</td><td>edu.iu.dsc.tws.comms.tcp.TWSTCPChannel</td><td></td></tr></tbody></table>

<h3>Resource Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.mesos.scheduler.working.directory</td><td>~/.twister2/repository"#"${'{TWISTER2_DIST}'}/topologies/${'{CLUSTER}'}/${'{ROLE}'}/${'{TOPOLOGY}'}</td><td>working directory for the topologies</td></tr><tr><td>twister2.resource.directory.core-package</td><td>/root/.twister2/repository/twister2-core/</td><td></td></tr><tr><td>twister2.resource.directory.sandbox.java.home</td><td>${'{JAVA_HOME}'}</td><td>location of java - pick it up from shell environment</td></tr><tr><td>twister2.mesos.master.uri</td><td>149.165.150.81:5050</td><td>The URI of Mesos Master</td></tr><tr><td>twister2.resource.mesos.framework.name</td><td>Twister2 framework</td><td>mesos framework name</td></tr><tr><td>twister2.resource.mesos.master.uri</td><td>zk://localhost:2181/mesos</td><td></td></tr><tr><td>twister2.resource.mesos.framework.staging.timeout.ms</td><td>2000</td><td>The maximum time in milliseconds waiting for MesosFramework got registered with Mesos Master</td></tr><tr><td>twister2.resource.mesos.scheduler.driver.stop.timeout.ms</td><td>5000</td><td>The maximum time in milliseconds waiting for Mesos Scheduler Driver to complete stop()</td></tr><tr><td>twister2.resource.mesos.native.library.path</td><td>/usr/lib/mesos/0.28.1/lib/</td><td>the path to load native mesos library</td></tr><tr><td>twister2.resource.system.package.uri</td><td>${'{TWISTER2_DIST}'}/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td>the core package uri</td></tr><tr><td>twister2.resource.mesos.overlay.network.name</td><td>mesos-overlay</td><td></td></tr><tr><td>twister2.resource.mesos.docker.image</td><td>gurhangunduz/twister2-mesos:docker-mpi</td><td></td></tr><tr><td>twister2.resource.system.job.uri</td><td>http://localhost:8082/twister2/mesos/twister2-job.tar.gz</td><td>the job package uri for mesos agent to fetch.<br/>For fetching http server must be running on mesos master</td></tr><tr><td>twister2.resource.class.launcher</td><td>edu.iu.dsc.tws.rsched.schedulers.mesos.MesosLauncher</td><td>launcher class for mesos submission</td></tr><tr><td>twister2.resource.job.worker.class</td><td>edu.iu.dsc.tws.examples.internal.comms.BroadcastCommunication</td><td>container class to run in workers</td></tr><tr><td>twister2.resource.class.mesos.worker</td><td>edu.iu.dsc.tws.rsched.schedulers.mesos.MesosWorker</td><td>the Mesos worker class</td></tr><tr><td>twister2.resource.uploader.directory</td><td>/var/www/html/twister2/mesos/</td><td>the directory where the file will be uploaded, make sure the user has the necessary permissions<br/>to upload the file here.</td></tr><tr><td>#twister2.resource.uploader.directory.repository</td><td>/var/www/html/twister2/mesos/</td><td></td></tr><tr><td>twister2.resource.uploader.scp.command.options</td><td>--chmod=+rwx</td><td>This is the scp command options that will be used by the uploader, this can be used to<br/>specify custom options such as the location of ssh keys.</td></tr><tr><td>twister2.resource.uploader.scp.command.connection</td><td>root@149.165.150.81</td><td>The scp connection string sets the remote user name and host used by the uploader.</td></tr><tr><td>twister2.resource.uploader.ssh.command.options</td><td></td><td>The ssh command options that will be used when connecting to the uploading host to execute<br/>command such as delete files, make directories.</td></tr><tr><td>twister2.resource.uploader.ssh.command.connection</td><td>root@149.165.150.81</td><td>The ssh connection string sets the remote user name and host used by the uploader.</td></tr><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.scp.ScpUploader</td><td>the uploader class<hr/><b>Options</b><ul><li>edu.iu.dsc.tws.rsched.uploaders.NullUploader</li><li>edu.iu.dsc.tws.rsched.uploaders.localfs.LocalFileSystemUploader</li></ul></td></tr><tr><td>twister2.resource.uploader.download.method</td><td>HTTP</td><td>this is the method that workers use to download the core and job packages<br/>it could be  HTTP, HDFS, ..</td></tr><tr><td>twister2.resource.HTTP.fetch.uri</td><td>http://149.165.150.81:8082</td><td>HTTP fetch uri</td></tr></tbody></table>

<h3>Client configuration parameters for submission of twister2 jobs</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.scheduler.mesos.cluster</td><td>example</td><td>cluster name mesos scheduler runs in</td></tr><tr><td>twister2.resource.scheduler.mesos.role</td><td>www-data</td><td>role in cluster</td></tr><tr><td>twister2.resource.scheduler.mesos.env</td><td>devel</td><td>environment name</td></tr><tr><td>twister2.resource.job.name</td><td>basic-mesos</td><td>mesos job name</td></tr><tr><td>  #  workersPerPod</td><td>2 # number of workers on each pod in Kubernetes. May be omitted in other clusters.</td><td>A Twister2 job can have multiple sets of compute resources<br/>instances shows the number of compute resources to be started with this specification<br/>workersPerPod shows the number of workers on each pod in Kubernetes.<br/>   May be omitted in other clusters. default value is 1.</td></tr><tr><td>    instances</td><td>4 # number of compute resource instances with this specification</td><td><hr/><b>Options</b><ul><li>2 # number of workers on each pod in Kubernetes. May be omitted in other clusters.</li></ul></td></tr><tr><td>twister2.resource.worker.additional.ports</td><td>["port1", "port2", "port3"]</td><td>by default each worker has one port<br/>additional ports can be requested for all workers in a job<br/>please provide the requested port names as a list</td></tr><tr><td>twister2.resource.job.driver.class</td><td>edu.iu.dsc.tws.examples.internal.rsched.DriverExample</td><td>driver class to run</td></tr><tr><td>twister2.resource.nfs.server.address</td><td>149.165.150.81</td><td>nfs server address</td></tr><tr><td>twister2.resource.nfs.server.path</td><td>/nfs/shared-mesos/twister2</td><td>nfs server path</td></tr><tr><td>twister2.resource.worker_port</td><td>31000</td><td>worker port</td></tr><tr><td>twister2.resource.desired_nodes</td><td>all</td><td>desired nodes</td></tr><tr><td>twister2.resource.use_docker_container</td><td>true</td><td></td></tr><tr><td>twister2.resource.rack.labey.key</td><td>rack</td><td>rack label key for Mesos nodes in a cluster<br/>each rack should have a unique label<br/>all nodes in a rack should share this label<br/>Twister2 workers can be scheduled by using these label values<br/>Better data locality can be achieved<br/>no default value is specified</td></tr><tr><td>twister2.resource.datacenter.labey.key</td><td>datacenter</td><td>data center label key<br/>each data center should have a unique label<br/>all nodes in a data center should share this label<br/>Twister2 workers can be scheduled by using these label values<br/>Better data locality can be achieved<br/>no default value is specified</td></tr><tr><td>  - echo</td><td>['blue-rack', 'green-rack']</td><td>Data center list with rack names</td></tr><tr><td>  - blue-rack</td><td>['10.0.0.40', '10.0.0.41', '10.0.0.42', '10.0.0.43', '10.0.0.44', ]</td><td>Rack list with node IPs in them</td></tr></tbody></table>

<h3>Core Configurations</h3>



<h3>Twister2 Job Master related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.job.master.runs.in.client</td><td>false</td><td>if true, the job master runs in the submitting client<br/>if false, job master runs as a separate process in the cluster<br/>by default, it is true<br/>when the job master runs in the submitting client, this client has to be submitting the job from a machine in the cluster</td></tr><tr><td>#twister2.job.master.port</td><td>2023</td><td>twister2 job master port number<br/>default value is 11111</td></tr><tr><td>twister2.worker.to.job.master.response.wait.duration</td><td>10000</td><td>worker to job master response wait time in milliseconds<br/>this is for messages that wait for a response from the job master<br/>default value is 10seconds = 10000</td></tr><tr><td>twister2.job.master.volatile.volume.size</td><td>1.0</td><td>twister2 job master volatile volume size in GB<br/>default value is 1.0 Gi<br/>if this value is 0, volatile volume is not setup for job master</td></tr><tr><td>twister2.job.master.persistent.volume.size</td><td>1.0</td><td>twister2 job master persistent volume size in GB<br/>default value is 1.0 Gi<br/>if this value is 0, persistent volume is not setup for job master</td></tr><tr><td>twister2.job.master.cpu</td><td>0.2</td><td>twister2 job master cpu request<br/>default value is 0.2 percentage</td></tr><tr><td>twister2.job.master.ram</td><td>1000</td><td>twister2 job master RAM request in MB<br/>default value is 0.2 percentage</td></tr><tr><td>twister2.job.master.ip</td><td>149.165.150.81</td><td></td></tr></tbody></table>

<h3>WorkerController related config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.worker.controller.max.wait.time.for.all.workers.to.join</td><td>100000</td><td>amount of timeout for all workers to join the job<br/>in milli seconds</td></tr><tr><td>twister2.worker.controller.max.wait.time.on.barrier</td><td>100000</td><td>amount of timeout on barriers for all workers to arrive<br/>in milli seconds</td></tr></tbody></table>

<h3>Dashboard related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.dashboard.host</td><td>http://localhost:8080</td><td>Dashboard server host address and port<br/>if this parameter is not specified, then job master will not try to connect to Dashboard</td></tr></tbody></table>

<h3>Task Configurations</h3>



No specific configurations
<h2>Nomad configurations</h2>
<h3>Checkpoint Configurations</h3>



No specific configurations
<h3>Data Configurations</h3>



No specific configurations
<h3>Network Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.network.channel.class</td><td>edu.iu.dsc.tws.comms.tcp.TWSTCPChannel</td><td></td></tr></tbody></table>

<h3>Resource Configurations</h3>



<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.scheduler.mpi.working.directory</td><td>${'{HOME}'}/.twister2/jobs</td><td>working directory</td></tr><tr><td>twister2.resource.job.package.url</td><td>http://149.165.xxx.xx:8082/twister2/mesos/twister2-job.tar.gz</td><td></td></tr><tr><td>twister2.resource.core.package.url</td><td>http://149.165.xxx.xx:8082/twister2/mesos/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td></td></tr><tr><td>twister2.resource.class.launcher</td><td>edu.iu.dsc.tws.rsched.schedulers.nomad.NomadLauncher</td><td>the launcher class</td></tr><tr><td>twister2.resource.nomad.scheduler.uri</td><td>http://localhost:4646</td><td></td></tr><tr><td>twister2.resource.nomad.core.freq.mapping</td><td>2000</td><td>The nomad schedules cpu resources in terms of clock frequency (e.g. MHz), while Heron topologies<br/>specify cpu requests in term of cores.  This config maps core to clock freqency.</td></tr><tr><td>twister2.resource.filesystem.shared</td><td>true</td><td>weather we are in a shared file system, if that is the case, each worker will not download the<br/>core package and job package, otherwise they will download those packages</td></tr><tr><td>twister2.resource.nomad.shell.script</td><td>nomad.sh</td><td>name of the script</td></tr><tr><td>twister2.resource.system.package.uri</td><td>${'{TWISTER2_DIST}'}/twister2-core-0.8.0-SNAPSHOT.tar.gz</td><td>path to the system core package</td></tr><tr><td>twister2.resource.uploader.directory</td><td>/root/.twister2/repository/</td><td>the directory where the file will be uploaded, make sure the user has the necessary permissions<br/>to upload the file here.<br/>if you want to run it on a local machine use this value</td></tr><tr><td>#twister2.resource.uploader.directory</td><td>/var/www/html/twister2/mesos/</td><td>if you want to use http server on echo</td></tr><tr><td>twister2.resource.uploader.scp.command.options</td><td>--chmod=+rwx</td><td>This is the scp command options that will be used by the uploader, this can be used to<br/>specify custom options such as the location of ssh keys.</td></tr><tr><td>twister2.resource.uploader.scp.command.connection</td><td>root@localhost</td><td>The scp connection string sets the remote user name and host used by the uploader.</td></tr><tr><td>twister2.resource.uploader.ssh.command.options</td><td></td><td>The ssh command options that will be used when connecting to the uploading host to execute<br/>command such as delete files, make directories.</td></tr><tr><td>twister2.resource.uploader.ssh.command.connection</td><td>root@localhost</td><td>The ssh connection string sets the remote user name and host used by the uploader.</td></tr><tr><td>twister2.resource.class.uploader</td><td>edu.iu.dsc.tws.rsched.uploaders.localfs.LocalFileSystemUploader</td><td>file system uploader to be used<hr/><b>Options</b><ul><li>edu.iu.dsc.tws.rsched.uploaders.scp.ScpUploader</li></ul></td></tr><tr><td>twister2.resource.uploader.download.method</td><td>LOCAL</td><td>this is the method that workers use to download the core and job packages<br/>it could be  LOCAL,  HTTP, HDFS, ..</td></tr></tbody></table>

<h3>client related configurations for job submit</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.resource.nfs.server.address</td><td>localhost</td><td>nfs server address</td></tr><tr><td>twister2.resource.nfs.server.path</td><td>/tmp/logs</td><td>nfs server path</td></tr><tr><td>twister2.resource.rack.labey.key</td><td>rack</td><td>rack label key for Mesos nodes in a cluster<br/>each rack should have a unique label<br/>all nodes in a rack should share this label<br/>Twister2 workers can be scheduled by using these label values<br/>Better data locality can be achieved<br/>no default value is specified</td></tr><tr><td>twister2.resource.datacenter.labey.key</td><td>datacenter</td><td>data center label key<br/>each data center should have a unique label<br/>all nodes in a data center should share this label<br/>Twister2 workers can be scheduled by using these label values<br/>Better data locality can be achieved<br/>no default value is specified</td></tr><tr><td>  - echo</td><td>['blue-rack', 'green-rack']</td><td>Data center list with rack names</td></tr><tr><td>  - green-rack</td><td>['node11.ip', 'node12.ip', 'node13.ip']</td><td>Rack list with node IPs in them</td></tr><tr><td>  #  workersPerPod</td><td>2 # number of workers on each pod in Kubernetes. May be omitted in other clusters.</td><td>A Twister2 job can have multiple sets of compute resources<br/>instances shows the number of compute resources to be started with this specification<br/>workersPerPod shows the number of workers on each pod in Kubernetes.<br/>   May be omitted in other clusters. default value is 1.</td></tr><tr><td>    instances</td><td>4 # number of compute resource instances with this specification</td><td><hr/><b>Options</b><ul><li>2 # number of workers on each pod in Kubernetes. May be omitted in other clusters.</li></ul></td></tr><tr><td>twister2.resource.worker.additional.ports</td><td>["port1", "port2", "port3"]</td><td>by default each worker has one port<br/>additional ports can be requested for all workers in a job<br/>please provide the requested port names as a list</td></tr><tr><td>twister2.resource.worker_port</td><td>31000</td><td>worker port</td></tr></tbody></table>

<h3>Core Configurations</h3>



<h3>Twister2 Job Master related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.job.master.runs.in.client</td><td>true</td><td>if true, the job master runs in the submitting client<br/>if false, job master runs as a separate process in the cluster<br/>by default, it is true<br/>when the job master runs in the submitting client, this client has to be submitting the job from a machine in the cluster</td></tr><tr><td>twister2.job.master.port</td><td>11011</td><td>twister2 job master port number<br/>default value is 11111</td></tr><tr><td>twister2.worker.to.job.master.response.wait.duration</td><td>10000</td><td>worker to job master response wait time in milliseconds<br/>this is for messages that wait for a response from the job master<br/>default value is 10seconds = 10000</td></tr><tr><td>twister2.job.master.volatile.volume.size</td><td>1.0</td><td>twister2 job master volatile volume size in GB<br/>default value is 1.0 Gi<br/>if this value is 0, volatile volume is not setup for job master</td></tr><tr><td>twister2.job.master.persistent.volume.size</td><td>1.0</td><td>twister2 job master persistent volume size in GB<br/>default value is 1.0 Gi<br/>if this value is 0, persistent volume is not setup for job master</td></tr><tr><td>twister2.job.master.cpu</td><td>0.2</td><td>twister2 job master cpu request<br/>default value is 0.2 percentage</td></tr><tr><td>twister2.job.master.ram</td><td>1000</td><td>twister2 job master RAM request in MB<br/>default value is 0.2 percentage</td></tr><tr><td>twister2.job.master.ip</td><td>localhost</td><td>the job master ip to be used, this is used only in client based masters</td></tr></tbody></table>

<h3>WorkerController related config parameters</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.worker.controller.max.wait.time.for.all.workers.to.join</td><td>1000000</td><td>amount of timeout for all workers to join the job<br/>in milli seconds</td></tr><tr><td>twister2.worker.controller.max.wait.time.on.barrier</td><td>1000000</td><td>amount of timeout on barriers for all workers to arrive<br/>in milli seconds</td></tr></tbody></table>

<h3>Dashboard related settings</h3>
<table><thead><tr><td width="25%">Name</td><td width="25%">Default</td><td width="50%">Description</td></tr></thead><tbody><tr><td>twister2.dashboard.host</td><td>http://localhost:8080</td><td>Dashboard server host address and port<br/>if this parameter is not specified, then job master will not try to connect to Dashboard</td></tr></tbody></table>

<h3>Task Configurations</h3>



No specific configurations

           
 </div>
        )
    }
}

module.exports = Configs;
