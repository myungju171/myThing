//
//  MyThingsApp.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

@main
struct MyThingsApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
          MyWishDetailView(index: 5)
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
