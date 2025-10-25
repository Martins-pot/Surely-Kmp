package com.sportmaster.surelykmp.utils

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UILabel
import platform.UIKit.UIWindow
import platform.Foundation.NSURL

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.Foundation.NSArray

actual class ShareManager {
    actual fun shareText(text: String, title: String?) {
        val activityItems = NSArray.arrayWithObject(text)

        val activityViewController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )

        // Get the root view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

        // For iPad support (prevents crash)
        activityViewController.popoverPresentationController?.sourceView =
            rootViewController?.view

        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}

@Composable
actual fun rememberShareManager(): ShareManager {
    return remember { ShareManager() }
}