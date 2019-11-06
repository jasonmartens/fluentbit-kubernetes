FROM ubuntu:latest
RUN apt-get update && apt-get install openssh-server -y

# SSH login fix. Otherwise user is kicked off after login
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

# Set up ssh
RUN mkdir -p /root/.ssh && chmod 700 /root/.ssh && \
	mkdir -p /run/sshd 
ADD id_rsa.pub /root/.ssh/authorized_keys
RUN chmod 600 /root/.ssh/authorized_keys

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]