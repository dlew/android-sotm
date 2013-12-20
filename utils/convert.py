# Prepares a set of source PNG images into the files used by SotM.
# 
# There are four folders output by this script:
#
# /drawable-ldpi/
# /drawable-mdpi/
# /drawable-hdpi/
# /drawable-xhdpi/
#
# WARNING: Deletes the output directory each time run!  Make sure nothing
# precious is in there!

import os
import shutil
from optparse import OptionParser

try:
    from PIL import Image
except:
    import Image

# Script automatically creates a folder for each density based on this list
DENSITIES = [
    ("ldpi", 0.75),
    ("mdpi", 1.0),
    ("hdpi", 1.5),
    ("xhdpi", 2.0),
    ("xxhdpi", 3.0),
]

# Crops an image into a square (centered on middle of picture)
def crop_square(im):
    left = 0
    right = width = im.size[0]
    top = 0
    bottom = height = im.size[1]

    if width > height:
        diff = (width - height) / 2
        left += diff
        right -= diff
    elif height > width:
        diff = (height - width) / 2
        top += diff
        bottom -= diff

    return im.crop((left, top, right, bottom))

def get_density_dir(out_dir, density):
    return os.path.join(out_dir, "drawable-%s" % density[0])

def convert(art_dir, out_dir, size, quality):
    # Clear the current output directory
    if os.path.exists(out_dir):
        shutil.rmtree(out_dir)

    # Create output directories
    for density in DENSITIES:
        os.makedirs(get_density_dir(out_dir, density))

    # Convert ALL the files
    for filename in os.listdir(art_dir):
        if filename.endswith(".png") or filename.endswith(".jpg"):
            path = os.path.join(art_dir, filename)
            im = Image.open(path)
            im = im.convert("RGB")
            
            # Create a version for each density
            square_im = crop_square(im)
            for density in DENSITIES:
                new_size = int(density[1] * size)
                resized_im = square_im.resize((new_size, new_size), Image.ANTIALIAS)
                out_file = os.path.join(get_density_dir(out_dir, density), filename)[:-4] + ".jpg"
                resized_im.save(out_file, "JPEG", quality=quality)

if __name__ == "__main__":
    usage = "usage: %prog [options]"
    parser = OptionParser(usage=usage)
    parser.add_option('-a', '--art', action="store", help="Input directory (with png art)", default="art/")
    parser.add_option('-o', '--out', action="store", help="Output directory", default="out/")
    parser.add_option('-s', '--size', action="store", type="int", help="Size of cropped/resized art (in dp)", default=64)
    parser.add_option('-q', '--quality', action="store", type="int", help="JPEG quality for cropped/resized art", default=90)

    (options, args) = parser.parse_args()

    convert(options.art, options.out, options.size, options.quality)
