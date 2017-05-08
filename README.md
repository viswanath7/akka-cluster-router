Objective
===================

The picture below shows the actors ecosystem within this application. It depicts the fact that the application supports nodes of two different roles namely 
* **_frontend_** and 
* **_backend_**
 
 When a **_Quote_** message is sent to the **_FrontEnd_** actor, it forwards it to one of the registered **_BackEnd_** actors of the cluster via configured **_backendRouter_**.
 
 ![Akka cluster router](doc/akka-cluster-router.png)
 
## Node roles
 
Not all nodes of a cluster need to perform the same function. There may be sub-sets catering a particular functionality termed as "role" of a node. The **roles** of a node is defined in the configuration property named `akka.cluster.roles`

Custer-aware routers can take nodes' roles into account to achieve distribution of responsibilities during deployment of actors.
  
## Cluster start-up

In our case, we start actors after the cluster has been initialised, members have joined, and the cluster has reached a certain size.

>In the configuration file, we define required number of members before the leader changes member status of **_Joining_** members to **_Up_**.
Please notice the following in **application.conf**:

`akka.cluster.min-nr-of-members = 3`

>In the configuration file, we also define required number of members of a certain role, before the leader changes member status of **_Joining_** members to **_Up_**.
Please notice the following in **application.conf**: 
```
    akka.cluster.role {
      frontend.min-nr-of-members = 1
      backend.min-nr-of-members = 2
    }
```

>In **FrontEnd.scala**, the actor is started in **_registerOnMemberUp_** callback, which will be invoked when the current member status is changed to **_Up_**. See
```
    Cluster(actorSystem) registerOnMemberUp {
          actorRef = actorSystem.actorOf(props, name = "frontend")
        }
```

## Load balancer 

The FrontEnd utilises akka's in-built routing strategy through a configurable load balancer. 

The adaptive load balancing is based on **_cluster metrics_**; which is used for BackEnd node selection. Routing decisions are based on system's health data such as 

 1. Used and maximum JVM heap memory
 2. System load
 3. CPU utilisation

## Deployment of load balancer

The load balancer is nothing more than a declarative actor named **_backendRouter_**; which is a child of frontend.
It is configured to forward the messages to destination with path **_/user/backend_** according to cluster metrics.
The relevant snippet from application.conf is as shown below.

```
    akka.actor.deployment {
        /frontend/backendRouter {
          # Router type provided by metrics extension.
          router = adaptive-group
          # Router parameter specific for metrics extension.
          # metrics-selector = heap + load + cpu
          metrics-selector = mix
          #
          nr-of-instances = 100
          routees.paths = ["/user/backend"]
          cluster {
            enabled = on
            use-role = backend
            allow-local-routees = off
          }
        }
      }
```