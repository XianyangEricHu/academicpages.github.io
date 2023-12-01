---
layout: archive
title: "Project"
permalink: /project/
author_profile: true
---

### Spotify Music Classification
**[Report]()** | Code

In this project, I built machine learning models based upon 45,000 songs from Spotify API. After careful preprocessing, train/test split, and normalization, I adopted dimensionality reduction and clustering methods including PCA, T-SNE, and MDS. Then, I used the silhouette score to determine the optimal number of clusters as 2 and added the result of k-means as an extra label to boost classification efficiency. Eventually, I classified 5,000 songs into 10 genres via 4 models (SVM/Neural Network/Random Forest/Adaboost) under the convention for supervised learning. Model performances were evaluated using the ROC curve and the highest AUC score achieved was 0.92/1.00.

### Solving PDE with Forward Time Centered Space Method
**[Demonstration]()** | Code

The finite difference method (FDM) is a numerical method used to solve partial differential equations for which there is no analytical solution. It involves approximating the derivative of the unknown function using a finite difference equation in a discrete set of points on a grid. The points are usually divided in a uniform manner (or equally spaced points to ensure that the discretization is consistent and the approximation is accurate. With a uniform grid, the spacing between the points is constant throughout the domain which maintains the regularity of the problem.
