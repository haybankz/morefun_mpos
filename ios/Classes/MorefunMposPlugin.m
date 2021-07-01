#import "MorefunMposPlugin.h"
#if __has_include(<morefun_mpos/morefun_mpos-Swift.h>)
#import <morefun_mpos/morefun_mpos-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "morefun_mpos-Swift.h"
#endif

@implementation MorefunMposPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMorefunMposPlugin registerWithRegistrar:registrar];
}
@end
