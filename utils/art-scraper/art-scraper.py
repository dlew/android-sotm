# Note: just save source to a file "source.html"
# Yes this is lazy, but so what

from BeautifulSoup import BeautifulSoup
import urllib

data = open("source.html")
soup = BeautifulSoup(data)
imgs = soup.findAll('img')

for img in imgs:
    src = img["src"]
    src = src.replace("styles/square_thumbnail/public/", "")
    end = src.rfind('?')
    start = src.rfind('/') + 1
    filename = src[start:end].replace("%20", "_").lower()
    filename = filename.replace("_0", "")
    filename = filename.replace("_1", "")
    filename = "card_" + filename
    print(filename)
    urllib.urlretrieve(src, filename)
