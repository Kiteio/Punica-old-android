package org.kiteio.punica.candy

/**
 * URL 合辑
 */
object URLs {
    /** Github */
    private const val GITHUB = "https://github.com"

    /** Gitee */
    private const val GITEE = "https://gitee.com"

    private const val PUNICA_ROUTE = "/Kiteio/Punica"

    /** Punica */
    const val PUNICA = "$GITHUB$PUNICA_ROUTE"

    /** Tessdata 训练文件 */
    const val TESSDATA = "$GITEE$PUNICA_ROUTE/raw/master/tessdata/eng.traineddata"
}