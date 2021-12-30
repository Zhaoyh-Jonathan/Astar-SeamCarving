# Astar-SeamCarving

Discovered in 2007, seam-carving is an elegant content-aware image resizing technique where the image is reduced in size by one pixel of height/width at a time. A vertical seam in an image is a path of pixels connected from the top to the bottom with one pixel in each row, while a horizontal seam is a path of pixels connected from the left to the right with one pixel in each column.

Unlike standard content-agnostic resizing techniques (such as cropping and scaling), seam carving preserves the most interest features (aspect ratio, set of objects present, etc.) of the image by calculating and removing seams with less energy.

To implement the algorithm, we did energy calculation (Dual-Gradient Energy Function), seam identification, and seam removal. The problem is reduced to a shortest path problem that we could solve using Dijkstra’s algorithm, or A* (star) search algorithm, with appropriately preprocessing and postprocessing the input/output data.
