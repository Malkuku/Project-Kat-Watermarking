FROM centos:7

RUN rm -rf /etc/yum.repos.d/* && \
    curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo && \
    sed -i 's/gpgcheck=1/gpgcheck=0/g' /etc/yum.repos.d/CentOS-Base.repo && \
    yum clean all && \
    yum makecache

RUN yum install -y wget tar glibc-common && \
yum clean all && \
localedef -i en_US -f UTF-8 en_US.UTF-8

RUN wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz -O /tmp/openjdk-17.tar.gz && \
    tar -xzf /tmp/openjdk-17.tar.gz -C /usr/local/ && \
    rm -f /tmp/openjdk-17.tar.gz && \
    ln -s /usr/local/jdk-17.* /usr/local/jdk

ENV JAVA_HOME=/usr/local/jdk
ENV PATH=$JAVA_HOME$/bin:$PATH
ENV LANG=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8

RUN mkdir -p /watermark/{logs}

COPY ./watermark/kat-watermarking.jar /watermark/

WORKDIR /watermark

EXPOSE 8080

ENTRYPOINT [ "java","-jar","kat-watermarking.jar" ]