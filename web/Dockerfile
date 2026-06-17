FROM ubuntu:24.04

# Install JDK 21, NGINX, SSH, Git, and Maven
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    nginx \
    openssh-server \
    git \
    maven \
    mysql-client \
    && rm -rf /var/lib/apt/lists/*

# Configure SSH to listen on port 2222
RUN mkdir /var/run/sshd
RUN echo 'root:root' | chpasswd
RUN sed -i 's/#Port 22/Port 2222/' /etc/ssh/sshd_config
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# Configure NGINX
COPY nginx.conf /etc/nginx/sites-available/default

# Add and configure the entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose the requested ports
EXPOSE 8443 2222

CMD ["/entrypoint.sh"]