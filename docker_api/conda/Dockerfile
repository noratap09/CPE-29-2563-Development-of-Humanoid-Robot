FROM continuumio/miniconda3

WORKDIR /conda 

# Make RUN commands use `bash --login`:
SHELL ["/bin/bash", "--login", "-c"]

RUN apt-get update ##[edited]
RUN apt-get install 'ffmpeg'\
    'libsm6'\ 
    'libxext6'  -y

# Create the environment:
COPY Web_API_comvis.yml .
RUN conda env create --name Web_API_comvis -f Web_API_comvis.yml

# Make RUN commands use the new environment:
SHELL ["conda", "run", "-n", "Web_API_comvis", "/bin/bash", "-c"]

# Initialize conda in bash config fiiles:
RUN conda init bash

# Activate the environment, and make sure it's activated:
RUN conda init bash

RUN conda activate Web_API_comvis > ~/.bashrc
RUN echo "Make sure flask is installed:"
RUN python -c "import flask"

# The code to run when container is started:
COPY App.py .
ENTRYPOINT ["conda", "run", "-n", "Web_API_comvis", "python", "App.py"]

