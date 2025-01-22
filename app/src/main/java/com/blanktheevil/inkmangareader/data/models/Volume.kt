package com.blanktheevil.inkmangareader.data.models

typealias Volumes = Map<VolumeNumber, Volume>
typealias Volume = Map<ChapterNumber, VolumeChapter>
typealias VolumeChapter = Map<ScanlationGroupId, Chapter>
typealias VolumeNumber = String
typealias ChapterNumber = String
typealias ScanlationGroupId = String
