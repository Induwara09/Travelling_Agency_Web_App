# 🌴 Sri Lanka Travel Agency – DevOps & Kubernetes Project



A full-stack Sri Lanka Travel Agency application enhanced with a complete DevOps workflow using **GitHub Actions, Docker, GitHub Container Registry, Kubernetes, k3d, Traefik, MySQL, Spring Boot Actuator, and Horizontal Pod Autoscaling**.



## 🚀 DevOps Features



- GitHub Actions CI/CD pipeline

- Maven automated backend build

- Dockerized Spring Boot backend

- Docker images stored in GitHub Container Registry (GHCR)

- Local Kubernetes cluster using k3d

- MySQL deployment with persistent storage

- Kubernetes ConfigMap and Secret management

- Backend Deployment with multiple replicas

- Kubernetes ClusterIP services

- Traefik Ingress routing

- Spring Boot Actuator health monitoring

- Kubernetes readiness probes

- Kubernetes liveness probes

- Horizontal Pod Autoscaler (HPA)

- Kubernetes self-healing

- Rolling deployments

- CPU-based automatic scaling



## 🏗 Architecture



```text

Developer

&#x20;  |

&#x20;  | git push

&#x20;  v

GitHub Repository

&#x20;  |

&#x20;  v

GitHub Actions CI/CD

&#x20;  |

&#x20;  | Build Spring Boot JAR

&#x20;  | Build Docker Image

&#x20;  v

GitHub Container Registry (GHCR)

&#x20;  |

&#x20;  v

Kubernetes / k3d Cluster

&#x20;  |

&#x20;  +----------------------+

&#x20;  |                      |

&#x20;  v                      v

Traefik Ingress       MySQL Service

&#x20;  |                      |

&#x20;  v                      v

Backend Service       MySQL Pod

&#x20;  |

&#x20;  v

Spring Boot Backend Pods

&#x20;  |

&#x20;  +--> Readiness Probe

&#x20;  +--> Liveness Probe

&#x20;  +--> HPA Autoscaling
