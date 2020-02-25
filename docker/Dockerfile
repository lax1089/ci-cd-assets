FROM jenkins/jenkins:lts
EXPOSE 8080
EXPOSE 5000

# Set the Jenkins environment to support Headless selenium testing
ENV JAVA_OPTS "-Djava.awt.headless=true -Dhudson.model.DirectoryBrowserSupport.CSP=\"default-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src 'self' 'unsafe-inline' data:;\""

# As the root user, install software
USER root

# Update the OS package manager
RUN apt-get -y update
RUN apt-get upgrade -y

# Download and install Chrome browser
RUN curl -sS -o - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add
RUN echo "deb [arch=amd64]  http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list
RUN apt-get -y update
RUN apt-get -y install google-chrome-stable

# Download Chromedriver
RUN wget https://chromedriver.storage.googleapis.com/2.41/chromedriver_linux64.zip
RUN unzip chromedriver_linux64.zip
RUN mv chromedriver /usr/bin/chromedriver
RUN chown root:root /usr/bin/chromedriver
RUN chmod +x /usr/bin/chromedriver

# Install the latest Firefox browser
RUN apt-get install -y firefox-esr 

# Download Geckodriver
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.25.0/geckodriver-v0.25.0-linux64.tar.gz
RUN tar -xvzf geckodriver*
RUN mv geckodriver /usr/bin/geckodriver
RUN chown root:root /usr/bin/geckodriver
RUN chmod +x /usr/bin/geckodriver

# Install the latest XVFB, Used for Selenium Headless testing
RUN apt-get install -y xvfb 

# Install Python 3 and the Python 3 Package Manager
RUN apt-get install -y python3
RUN apt-get install -y python3-pip

# Install Taurus
RUN pip3 install locustio==0.13.5
RUN pip3 install bzt